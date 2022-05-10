package org.forza.protocol.handler;

import org.forza.common.DecodeableInvocation;
import org.forza.common.command.CommandFactory;
import org.forza.common.command.RemotingCommand;
import org.forza.common.command.RequestCommand;
import org.forza.common.command.ResponseCommand;
import org.forza.common.enums.ResponseStatus;
import org.forza.common.exception.RemotingException;
import org.forza.reomoting.Connection;
import org.forza.reomoting.DefaultFuture;
import org.forza.reomoting.RemotingContext;
import org.forza.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public abstract class AbstractCommandHandler<T extends RemotingCommand> implements CommandHandler<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCommandHandler.class);
    protected CommandFactory commandFactory;

    public AbstractCommandHandler() {
        this.commandFactory = new CommandFactory();
    }

    @Override
    public boolean handelInIOThread() {
        return true;
    }

    @Override
    public void handle(RemotingContext ctx, T cmd) throws RemotingException {
        ExecutorService executor = handelInIOThread()
                ? ctx.getEventLoop() : ctx.protocolExecutor();

        // IO thread decode className
        DecodeableInvocation inv = (DecodeableInvocation) cmd.getInvocation();
        inv.decodeClassName();
        try {
            executor.execute(new HandlerRunnable(ctx, cmd));
        } catch (Throwable e) {
            throw new RemotingException(ctx.getConnection(), ObjectUtils.toString(e));
        }


    }

    class HandlerRunnable implements Runnable {
        private RemotingContext context;
        private T command;

        public HandlerRunnable(RemotingContext context, T command) {
            this.context = context;
            this.command = command;
        }

        @Override
        public void run() {
            DecodeableInvocation invocation = (DecodeableInvocation) command.getInvocation();
            // IO thread or biz thread decode data
            invocation.decodeData();
            try {
                Connection connection = context.getConnection();
                if (command instanceof RequestCommand) {
                    RequestCommand request = (RequestCommand) command;
                    if (request.isBroken()) {
                        Throwable t = (Throwable) invocation.getData();
                        ResponseCommand response = commandFactory.createResponse(request, ResponseStatus.SERVER_EXCEPTION, ObjectUtils.toString(t));
                        connection.sendResponseIfNecessary(request, response);
                        return;
                    }
                    try {
                        Object result = handleRequest(context, request);
                        // 服务端异步支持
                        if (result instanceof CompletableFuture) {
                            CompletableFuture<Object> future = (CompletableFuture) result;
                            future.whenComplete((r, t) -> {
                                if (t != null) {
                                    connection.sendResponseIfNecessary(request,
                                            commandFactory.createResponse(request, ResponseStatus.SERVER_EXCEPTION, ObjectUtils.toString(t)));
                                }
                                if (r != null) {
                                    connection.sendResponseIfNecessary(request, commandFactory.createResponse(request, result));
                                }
                            });
                            return;
                        }
                        if (result != null) {
                            connection.sendResponseIfNecessary(request, commandFactory.createResponse(request, result));
                        }
                    } catch (Throwable t) {
                        connection.sendResponseIfNecessary(request,
                                commandFactory.createResponse(request, ResponseStatus.SERVER_EXCEPTION, ObjectUtils.toString(t)));
                    }
                } else if (command instanceof ResponseCommand) {
                    handleResponse(context, (ResponseCommand) command);
                }
            } catch (Throwable e) {
                logger.warn("HandlerRunnable handle {} operation error, connection is {}", command, context.getConnection(), e);
            }
        }
    }

    public abstract <T> T handleRequest(RemotingContext ctx, RequestCommand request) throws Exception;

    protected void handleResponse(RemotingContext ctx, ResponseCommand response) {
        if (response.isHeartbeat()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Received heartbeat response from remote connection " + ctx.getConnection());
            }
        }
        Connection connection = ctx.getConnection();
        DefaultFuture.received(connection, response);
    }

}
