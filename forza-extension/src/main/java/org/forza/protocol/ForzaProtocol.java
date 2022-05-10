package org.forza.protocol;

import org.forza.common.command.CommandCode;
import org.forza.protocol.handler.CommandHandler;
import org.forza.protocol.handler.CommandHandlerManager;
import org.forza.util.NamedThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ForzaProtocol implements Protocol {
    private final CommandHandlerManager cmdHandlerManager;
    private ExecutorService executor;

    public ForzaProtocol() {
        this.cmdHandlerManager = new CommandHandlerManager();

        this.executor = new ThreadPoolExecutor(8,
                16,
                20,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(60),
                new NamedThreadFactory("Bolt-protocol-executor", true));
    }

    @Override
    public CommandHandler getCommandHandler(CommandCode cmdCode) {
        return this.cmdHandlerManager.getCmdHandler(cmdCode);
    }

    @Override
    public ExecutorService getDefaultExecutor() {
        return this.executor;
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

}
