package org.forza.autoconfigure;

import org.forza.common.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "forza")
@Data
public class ForzaProperties {
    private Client client = new Client();
    private Server server = new Server();

    /** 序列化协议 */
    private String serialization = "hessian2";
    /** TCP不延迟 */
    private boolean tcpNodelay = true;
    /** TCP套接字重新使用地址 */
    private boolean tcpSoReuseaddr = true;
    /** TCP套接字是否保持长连接 */
    private boolean tcpSoKeepalive = false;
    /** Netty IO速率 */
    private int nettyIORatio = 70;
    /** 是否启用Netty缓存池 */
    private boolean nettyBufferPooled = true;
    /** Netty缓冲高标记位 */
    private int nettyBufferHighWatermark = 64 * 1024;
    /** Netty缓冲低标记位 */
    private int nettyBufferLowerWatermark = 32 * 1024;
    /** Netty线程默认IO线程 */
    private int nettyServerIoThread = Constants.DEFAULT_IO_THREADS;

    @Data
    public static class Client {
        /** 启动client */
        private boolean enabled = false;
        private String host = "127.0.0.1";
        private int port = 8091;
        /** 连接超时 */
        private int connectTimeout = 3000;
        private int timeout =3000;
        /** 心跳检测间隔 */
        private int heartbeatInterval = 15 * 1000;
        /** 最大连接数 */
        private int maxConnection = 1;
        /**  */
        private int maxPendingAcquires = Integer.MAX_VALUE;
        /** 请求超时时间 */
        private long acquireTimeout = 3000;
        /** 请求超时操作 */
        private String acquireTimeoutAction = "new";
        /** LRU */
        private boolean lastRecentUsed = false;
        /** 是否心跳检测 */
        private boolean healthCheck = true;

    }

    @Data
    public static class Server {
        private boolean enabled = false;
        private int port = 8091;
    }

}
