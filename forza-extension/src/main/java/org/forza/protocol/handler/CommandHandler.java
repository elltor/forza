package org.forza.protocol.handler;

import org.forza.common.command.CommandCode;
import org.forza.common.exception.RemotingException;
import org.forza.common.extension.Extension;
import org.forza.reomoting.RemotingContext;

@Extension
public interface CommandHandler<T> {

    CommandCode getCommandCode();

    void handle(RemotingContext ctx, T command) throws RemotingException;

    boolean handelInIOThread();
}
