package org.forza.transport;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.forza.common.Url;
import org.forza.common.command.RemotingCommand;
import org.forza.common.command.RequestCommand;
import org.forza.common.command.ResponseCommand;
import org.forza.common.enums.CommandCodeEnum;
import org.forza.common.enums.ConnectionEventType;
import org.forza.common.enums.ResponseStatus;
import org.forza.common.exception.ExecutionException;
import org.forza.protocol.Protocol;
import org.forza.protocol.handler.CommandHandler;
import org.forza.protocol.handler.HeartbeatHandler;
import org.forza.reomoting.Connection;
import org.forza.reomoting.ConnectionEventListener;
import org.forza.reomoting.RemotingContext;
import org.forza.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;

/**
 * 将协议和URL认为一个 endpoint
 * Server 和 Client 共用
 */
@ChannelHandler.Sharable
public class ForzaHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(ForzaHandler.class);
    private final Protocol protocol;
    private Url url;
    private final boolean serverSide;

    private ConnectionEventListener eventListener;

    private ReconnectClient reconnectClient;

    public ForzaHandler(Url url, Protocol protocol, boolean serverSide) {
        this.url = url;
        this.protocol = protocol;
        this.serverSide = serverSide;
    }

    public void setConnectionEventListener(ConnectionEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setReconnectClient(ReconnectClient reconnectClient) {
        this.reconnectClient = reconnectClient;
    }

    /**
     * 激活连接，第一次连接调用此方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 缓存连接
        Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
        // 添加监听器
        protocol.getDefaultExecutor().execute(() -> {
            eventListener.onEvent(ConnectionEventType.CONNECT, connection);
        });

        if (logger.isInfoEnabled()) {
            logger.info("The connection of " + connection.getLocalAddress() + " -> " + connection.getRemoteAddress() + " is established.");
        }
    }

    /**
     * 连接失活，调用此方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 取出连接
        Connection connection = Connection.getOrAddConnection(ctx.channel(), url);

        // 处理关闭连接
        try {
            protocol.getDefaultExecutor().execute(() -> {
                eventListener.onEvent(ConnectionEventType.CLOSE, connection);
            });

            if (logger.isInfoEnabled()) {
                logger.info("The connection of " + connection.getLocalAddress() + " -> " + connection.getRemoteAddress() + " is disconnected.");
            }
        } finally {
            Connection.removeChannelIfDisconnected(ctx.channel());
        }
    }

    /**
     * 读数据事件
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
        RemotingCommand command = (RemotingCommand) msg;
        try {
            CommandHandler handler = protocol.getCommandHandler(command.getCmdCode());
            handler.handle(new RemotingContext(ctx, ctx.channel().eventLoop(),
                    protocol.getDefaultExecutor(), connection), command);
        } catch (Throwable t) {
            // 要处理使用业务线程池被打满没有返回值，导致客户端超时的情况
            if (command instanceof RequestCommand && t instanceof RejectedExecutionException) {
                String errorMsg = "Server side(" + url.getHost() + "," + url.getPort() + ") threadpool is exhausted ,detail msg:" + t.getMessage();
                RequestCommand request = (RequestCommand) command;
                ResponseCommand response = new ResponseCommand(request.getId(), request.getCmdCode());
                response.setStatus(ResponseStatus.SERVER_THREADPOOL_BUSY);
                response.setErrorMessage(errorMsg);
                connection.sendResponseIfNecessary(request, response);
                return;
            }
            throw new ExecutionException(command, connection, t.getMessage());
        } finally {
            Connection.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
        try {
            if (cause instanceof ExecutionException) {
                ExecutionException e = (ExecutionException) cause;
                RemotingCommand command = e.getCommand();
                if (command instanceof RequestCommand) {
                    RequestCommand request = (RequestCommand) command;
                    ResponseCommand response = new ResponseCommand(request.getId(), request.getCmdCode());
                    response.setStatus(ResponseStatus.SERVER_EXCEPTION);
                    response.setErrorMessage(ObjectUtils.toString(e));
                    connection.sendResponseIfNecessary(request, response);
                }
            }
            logger.warn(ObjectUtils.toString(cause));
        } finally {
            Connection.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
            try {
                if (serverSide) {
                    // 直接关闭连接,等待下次调用时重新建立连接
                    connection.close();
                } else {

                    HeartbeatHandler handler = (HeartbeatHandler) protocol.getCommandHandler(CommandCodeEnum.HEARTBEAT_CMD);
                    handler.setReconnectClient(reconnectClient);
                    handler.sendHeartbeat(connection);
                }
            } finally {
                Connection.removeChannelIfDisconnected(ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
