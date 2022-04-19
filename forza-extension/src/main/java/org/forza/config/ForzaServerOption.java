package org.forza.config;

/**
 * @Author:  
 * @DateTime: 2020/4/21
 * @Description: TODO
 */
public class ForzaServerOption<T> extends ForzaOption<T> {
    public static final ForzaOption<Integer> PORT = valueOf("bolt.server.port",8090);

    protected ForzaServerOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
