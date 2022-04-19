package org.forza.common.command;

/**
 * @Author:  
 * @DateTime: 2020/4/11
 * @Description: TODO
 */
public abstract class Command {
    private final transient CommandCode cmdCode;


    public Command(CommandCode cmdCode) {
        this.cmdCode = cmdCode;
    }

    protected CommandCode cmdCode() {
        return this.cmdCode;
    }

}
