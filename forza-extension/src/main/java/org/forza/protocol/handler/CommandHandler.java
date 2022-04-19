package org.forza.protocol.handler;

import org.forza.common.command.CommandCode;
import org.forza.common.exception.RemotingException;
import org.forza.common.extension.Extension;
import org.forza.reomoting.RemotingContext;


/**
 * @Author:  
 * @DateTime: 2020/4/4
 * @Description: TODO
 */
@Extension
public interface CommandHandler<T> {

    CommandCode getCommandCode();

    void handle(RemotingContext ctx, T command) throws RemotingException;

    boolean handelInIOThread();
}
