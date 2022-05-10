package org.forza.common.enums;

import org.forza.common.command.CommandCode;

public enum CommandCodeEnum implements CommandCode {
    GENERAL_CMD((short) 0),
    HEARTBEAT_CMD((short) 1);


    private short value;

    CommandCodeEnum(short value) {
        this.value = value;
    }

    @Override
    public short getValue() {
        return this.value;
    }

    public static CommandCodeEnum toEnum(short value) {
        for (CommandCodeEnum cmd : values()) {
            if (value == cmd.getValue()) {
                return cmd;
            }
        }
        throw new IllegalArgumentException("Unknown command code value: " + value);
    }
}
