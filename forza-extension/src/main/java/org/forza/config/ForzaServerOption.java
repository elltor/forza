package org.forza.config;

public class ForzaServerOption<T> extends ForzaOption<T> {
    public static final ForzaOption<Integer> PORT = valueOf("bolt.server.port",8090);

    protected ForzaServerOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
