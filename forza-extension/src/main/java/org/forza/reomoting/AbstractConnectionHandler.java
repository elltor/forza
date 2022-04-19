package org.forza.reomoting;

import io.netty.channel.ChannelFuture;
import org.forza.common.command.RemotingCommand;
import org.forza.common.command.RequestCommand;
import org.forza.common.command.ResponseCommand;
import org.forza.common.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求处理
 * @Author:  
 * @DateTime: 2020/4/24
 * @Description: TODO
 */
public abstract class AbstractConnectionHandler implements ConnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConnectionHandler.class);

    public <T> FutureAdapter<T> send(RequestCommand request, int timeout){
        DefaultFuture future = DefaultFuture.newFuture(getConnection(), request, timeout);
        FutureAdapter<T> futureAdapter = new FutureAdapter<>(future);
        writeAndFlush(request).addListener(f->{
            if (f.isSuccess()) {
                future.sent();
            }
            if (!f.isSuccess()) {
                // 取消
                future.cancel();
                throw new RemotingException(getConnection(), createErrorMsg(getConnection(), request, f.cause()));
            }
        });
        return futureAdapter;

    }

    public void sendResponseIfNecessary(RequestCommand request, ResponseCommand response) {
        if (request.isTwoWay()) {
            Connection connection = getConnection();
            connection.writeAndFlush(response).addListener(f -> {
                if (f.isSuccess()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("write response " + response.toString() +
                                "to " + getConnection().getRemoteAddress());
                    }
                }
                if (f.cause() != null) {
                    logger.error("write response " + response.toString() + "to " + connection.getRemoteAddress() + " error", f.cause());
                }
            });
        }
    }


    private String createErrorMsg(Connection connection, RemotingCommand cmd, Throwable t) {
        StringBuffer errorMsg = new StringBuffer();
        errorMsg.append("Failed to send message " + cmd.toString() + " to "
                + connection.getRemoteAddress() + ", request id " + cmd.getId());
        if (t != null) {
            errorMsg.append(", error message is:" + t.getMessage());
        }
        return errorMsg.toString();
    }

    abstract ChannelFuture writeAndFlush(Object msg);
}
