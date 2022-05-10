package org.forza.common.command;

public abstract class Command {
    private final transient CommandCode cmdCode;


    public Command(CommandCode cmdCode) {
        this.cmdCode = cmdCode;
    }

    protected CommandCode cmdCode() {
        return this.cmdCode;
    }

}
