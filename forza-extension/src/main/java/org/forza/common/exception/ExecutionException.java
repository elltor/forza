package org.forza.common.exception;

import org.forza.common.command.RemotingCommand;
import org.forza.reomoting.Connection;

import java.net.InetSocketAddress;

public class ExecutionException extends RemotingException {
    private static final long serialVersionUID = 5883088121519596889L;
    private final RemotingCommand command;

    public ExecutionException(RemotingCommand command, Connection connection, String msg) {
        super(connection, msg);
        this.command = command;
    }

    public ExecutionException(RemotingCommand command, InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message) {
        super(localAddress, remoteAddress, message);
        this.command = command;

    }

    public ExecutionException(RemotingCommand command, Connection connection, Throwable cause) {
        super(connection, cause);
        this.command = command;

    }

    public ExecutionException(RemotingCommand command, InetSocketAddress localAddress, InetSocketAddress remoteAddress, Throwable cause) {
        super(localAddress, remoteAddress, cause);
        this.command = command;

    }

    public ExecutionException(RemotingCommand command, Connection connection, String message, Throwable cause) {
        super(connection, message, cause);
        this.command = command;

    }

    public ExecutionException(RemotingCommand command, InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message, Throwable cause) {
        super(localAddress, remoteAddress, message, cause);
        this.command = command;
    }

    public RemotingCommand getCommand() {
        return command;
    }
}
