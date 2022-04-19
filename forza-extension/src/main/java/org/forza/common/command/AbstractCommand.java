package org.forza.common.command;

import lombok.Data;
import org.forza.common.Invocation;

/**
 * @Author:  
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
@Data
public abstract class AbstractCommand implements RemotingCommand {
    private int id;
    private CommandCode cmdCode;
    private boolean heartbeat;
    private String version;
    private Invocation invocation;

    public AbstractCommand() {

    }

    public AbstractCommand(CommandCode cmdCode) {
        this(0, cmdCode);
    }

    public AbstractCommand(int id, CommandCode cmdCode) {
        this.cmdCode = cmdCode;
        this.id = id;
    }

    @Override
    public Invocation getInvocation() {
        return this.invocation;
    }

    @Override
    public CommandCode getCmdCode() {
        return this.cmdCode;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
