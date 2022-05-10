package org.forza.common.command;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class RequestCommand extends AbstractCommand {
    private boolean twoWay = true;
    private boolean broken = false;
    private int timeout;
    private static final AtomicInteger INVOKE_ID = new AtomicInteger(0);

    public RequestCommand() {

    }

    public RequestCommand(CommandCode cmdCode) {
        super(newId(), cmdCode);
    }

    private static int newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return INVOKE_ID.getAndIncrement();
    }


    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "RequestCommand{id=" + getId() +
                " cmdCode=" + getCmdCode() +
                " isHeartBeat=" + isHeartbeat() + "}";
    }

}
