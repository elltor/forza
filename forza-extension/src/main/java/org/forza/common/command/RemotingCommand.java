package org.forza.common.command;

import org.forza.common.Invocation;


public interface RemotingCommand {

    CommandCode getCmdCode();

    int getId();

    Invocation getInvocation();

}
