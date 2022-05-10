package org.forza.common;

public final class Version {
    // TODO fix
    public static final String DEFAULT_BOLT_PROTOCOL_VERSION = "1.0.0";
    private Version(){

    }
    public static String getProtocolVersion() {
        return DEFAULT_BOLT_PROTOCOL_VERSION;
    }
}
