package org.forza.config;

import org.forza.common.Constants;

/**
 * @Author:  
 * @DateTime: 2020/4/10
 * @Description: TODO
 */
public class ForzaGenericOption<T> extends ForzaOption<T> {
    public static final ForzaOption<Boolean> TCP_NODELAY = valueOf("bolt.tcp.nodelay",true);
    public static final ForzaOption<Boolean> TCP_SO_REUSEADDR = valueOf("bolt.tcp.so.reuseaddr",true);
    public static final ForzaOption<Boolean> TCP_SO_KEEPALIVE = valueOf("bolt.tcp.so.keepalive",true);
    public static final ForzaOption<Integer> NETTY_IO_RATIO = valueOf("bolt.netty.io.ratio",70);
    public static final ForzaOption<Boolean> NETTY_BUFFER_POOLED = valueOf("bolt.netty.buffer.pooled",true);
    public static final ForzaOption<Integer> NETTY_BUFFER_HIGH_WATER_MARK = valueOf("bolt.netty.buffer.high.watermark",64 * 1024);
    public static final ForzaOption<Integer> NETTY_BUFFER_LOW_WATER_MARK = valueOf("bolt.netty.buffer.low.watermark",32 * 1024);
    public static final ForzaOption<Integer> IO_THREADS = valueOf("bolt.netty.server.io.thread", Constants.DEFAULT_IO_THREADS);

    protected ForzaGenericOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
