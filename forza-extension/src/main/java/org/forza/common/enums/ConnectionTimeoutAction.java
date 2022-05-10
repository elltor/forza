package org.forza.common.enums;

public enum ConnectionTimeoutAction {
    NEW("new"),
    FIAL("fail");
    private String value;

    ConnectionTimeoutAction(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}
