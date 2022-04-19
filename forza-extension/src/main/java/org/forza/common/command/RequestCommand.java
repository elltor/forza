package org.forza.common.command;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:  
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
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

    @Override
    public String toString() {
        return "RequestCommand{id=" + getId() +
                " cmdCode=" + getCmdCode() +
                " isHeartBeat=" + isHeartbeat() + "}";
    }

    private static int newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return INVOKE_ID.getAndIncrement();
    }



}
