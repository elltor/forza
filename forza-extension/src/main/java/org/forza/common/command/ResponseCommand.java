package org.forza.common.command;

import org.forza.common.enums.ResponseStatus;

public class ResponseCommand extends AbstractCommand {
    private ResponseStatus status;
    private String errorMessage;

    public ResponseCommand(CommandCode cmdCode) {
        super(cmdCode);
    }

    public ResponseCommand(int id, CommandCode cmdCode) {
        super(id, cmdCode);
    }


    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {

        return "ResponseCommand{status=" + status +
                " id=" + getId() +
                " cmdCode=" + getCmdCode() +
                " isHeartBeat" + isHeartbeat() + "}";
    }
}
