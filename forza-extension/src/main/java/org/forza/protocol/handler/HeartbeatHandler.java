package org.forza.protocol.handler;

import io.netty.util.Attribute;
import org.forza.common.Constants;
import org.forza.common.Version;
import org.forza.common.command.CommandCode;
import org.forza.common.command.RequestCommand;
import org.forza.common.command.ResponseCommand;
import org.forza.common.enums.CommandCodeEnum;
import org.forza.common.enums.ResponseStatus;
import org.forza.reomoting.Connection;
import org.forza.reomoting.RemotingContext;
import org.forza.transport.ReconnectClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HeartbeatHandler extends AbstractCommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);
    private Lock connectionLock = new ReentrantLock();
    private ReconnectClient reconnectClient;

    @Override
    public Object handleRequest(RemotingContext ctx, RequestCommand request) throws Exception {
        ResponseCommand response = new ResponseCommand(request.getId(), request.getCmdCode());
        response.setStatus(ResponseStatus.SUCCESS);
        response.setHeartbeat(true);
        response.setVersion(Version.getProtocolVersion());
        if (logger.isDebugEnabled()) {
            logger.debug("Received heartbeat from remote connection " + ctx.getConnection());
        }
        return response;
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCodeEnum.HEARTBEAT_CMD;
    }

    public void sendHeartbeat(Connection connection) {
        // 心跳次数
        Integer heartbeatCount = connection.attr(Connection.HEARTBEAT_COUNT).get();
        Integer maxCount = connection.getUrl().getParameter(Constants.MAX_HEARTBEAT_COUNT,
                Constants.DEFAULT_MAX_HEARTBEAT_COUNT);
        if (heartbeatCount > maxCount) {
            // 关闭连接并重连
            connectionLock.lock();
            try {
                if (connection.isActive()) {
                    connection.close();
                }
                if(!connection.isActive()){
                    reconnectClient.reconnect(connection.getUrl());
                }
            } finally {
                connectionLock.unlock();
            }

        } else {
            RequestCommand request = new RequestCommand(CommandCodeEnum.HEARTBEAT_CMD);
            request.setTwoWay(true);
            request.setVersion(Version.getProtocolVersion());
            request.setHeartbeat(true);

            connection.send(request, 1000).whenComplete(((res, cause) -> {
                if (cause != null) {
                    Integer oldCount;
                    Integer newCount;
                    Attribute<Integer> HEARTBEAT_COUNT = connection.attr(Connection.HEARTBEAT_COUNT);
                    do {
                        oldCount = HEARTBEAT_COUNT.get();
                        newCount = oldCount + 1;
                    } while (!HEARTBEAT_COUNT.compareAndSet(oldCount, newCount));
                    return;
                }
            }));

            if (logger.isDebugEnabled()) {
                logger.debug("Send heartbeat to remote connection " + connection + ", heartbeat times " + connection.attr(Connection.HEARTBEAT_COUNT).get());
            }
        }
    }

    public void setReconnectClient(ReconnectClient reconnectClient) {
        this.reconnectClient = reconnectClient;
    }
}
