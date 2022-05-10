package org.forza.common.command;

import org.forza.common.Invocation;


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

    public void setId(int id) {
        this.id = id;
    }

    public void setCmdCode(CommandCode cmdCode) {
        this.cmdCode = cmdCode;
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }
}
