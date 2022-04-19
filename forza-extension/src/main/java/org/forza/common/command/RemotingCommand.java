package org.forza.common.command;

import org.forza.common.Invocation;

/**
 * @Author:  
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
public interface RemotingCommand {

    CommandCode getCmdCode();

    int getId();

    Invocation getInvocation();

}
