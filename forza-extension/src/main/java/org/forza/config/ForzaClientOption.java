package org.forza.config;

import org.forza.common.Constants;

public class ForzaClientOption<T> extends ForzaRemotingOption<T> {

    public static final ForzaOption<String> HOST = valueOf(ForzaClientOption.class, "host", "127.0.0.1");
    public static final ForzaOption<Integer> PORT = valueOf(ForzaClientOption.class, "port", 8091);

    // <-----通信模型------>
    public static final ForzaOption<Boolean> ASYNC = valueOf(ForzaClientOption.class, Constants.ASYNC_KEY, false);

    // <-----空闲检测------>
    public static final ForzaOption<Integer> HEARTBEATINTERVAL = valueOf(ForzaClientOption.class, Constants.HEARTBEAT_KEY, Constants.DEFAULT_HEARTBEAT);
    // 全局请求超时时间
    public static final ForzaOption<Integer> TIMEOUT = valueOf(ForzaClientOption.class, Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
    // 全局连接超时时间
    public static final ForzaOption<Integer> CONNECT_TIMEOUT = valueOf(ForzaClientOption.class, Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT);

    // <-----连接池配置------>
    public static final ForzaOption<Integer> MAX_CONNECTION = valueOf(ForzaClientOption.class, Constants.MAX_CONNECTION, Constants.DEFAULT_MAX_CONNECTION);
    public static final ForzaOption<Integer> MAX_PENDING_ACQUIRES = valueOf(ForzaClientOption.class, Constants.MAX_PENDING_ACQUIRES, Constants.DEFAULT_MAX_PENDING_ACQUIRES);
    public static final ForzaOption<Long> ACQUIRE_TIMEOUT = valueOf(ForzaClientOption.class, Constants.ACQUIRE_TIMEOUT, Constants.DEFAULT_ACQUIRE_TIMEOUT);
    public static final ForzaOption<String> ACQUIRE_TIMEOUT_ACTION = valueOf(ForzaClientOption.class, Constants.ACQUIRE_TIMEOUT_ACTION, Constants.DEFAULT_ACQUIRE_TIMEOUT_ACTION);
    public static final ForzaOption<Boolean> RELEASE_HEALTH_CHECK = valueOf(ForzaClientOption.class, Constants.RELEASE_HEALTH_CHECK, Constants.DEFAULT_RELEASE_HEALTH_CHECK);
    public static final ForzaOption<Boolean> CONNECTION_LAST_RECENT_USED = valueOf(ForzaClientOption.class, Constants.CONNECTION_LAST_RECENT_USED, Constants.DEFAULT_CONNECTION_LAST_RECENT_USED);


    protected ForzaClientOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
