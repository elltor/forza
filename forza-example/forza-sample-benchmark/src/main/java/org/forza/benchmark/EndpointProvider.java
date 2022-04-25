package org.forza.benchmark;

import org.forza.benchmark.pojo.ForzaProperties;
import org.forza.config.ForzaClientOption;
import org.forza.config.ForzaGenericOption;
import org.forza.config.ForzaRemotingOption;
import org.forza.config.ForzaServerOption;
import org.forza.transport.Client;
import org.forza.transport.ForzaClient;
import org.forza.transport.ForzaServer;
import org.forza.transport.Server;


public class EndpointProvider {

    private ForzaProperties properties;

    public EndpointProvider() {
        this.properties = new ForzaProperties();
    }

    public Client client() {
        ForzaProperties.Client pc = properties.getClient();



        ForzaClient client = new ForzaClient();
        client.option(ForzaClientOption.HOST, pc.getHost())
                .option(ForzaClientOption.PORT, pc.getPort())
                .option(ForzaGenericOption.TCP_NODELAY, properties.isTcpNodelay())
                .option(ForzaGenericOption.TCP_SO_REUSEADDR, properties.isTcpSoReuseaddr())
                .option(ForzaGenericOption.NETTY_BUFFER_HIGH_WATER_MARK, properties.getNettyBufferHighWatermark())
                .option(ForzaGenericOption.NETTY_BUFFER_LOW_WATER_MARK, properties.getNettyBufferLowerWatermark())
                .option(ForzaGenericOption.NETTY_IO_RATIO, properties.getNettyIORatio())
                .option(ForzaGenericOption.TCP_SO_KEEPALIVE, properties.isTcpSoKeepalive())
                .option(ForzaClientOption.HEARTBEATINTERVAL, pc.getHeartbeatInterval())
                .option(ForzaClientOption.CONNECT_TIMEOUT, pc.getConnectTimeout())
                .option(ForzaClientOption.TIMEOUT, pc.getTimeout())
                .option(ForzaClientOption.MAX_CONNECTION, pc.getMaxConnection())
                .option(ForzaClientOption.ACQUIRE_TIMEOUT, pc.getAcquireTimeout())
                .option(ForzaClientOption.ACQUIRE_TIMEOUT_ACTION, pc.getAcquireTimeoutAction())
                .option(ForzaClientOption.RELEASE_HEALTH_CHECK, pc.isHealthCheck())
                .option(ForzaClientOption.CONNECTION_LAST_RECENT_USED, pc.isLastRecentUsed())
                .option(ForzaRemotingOption.SERIALIZATION, properties.getSerialization());
        client.startUp();
        return client;
    }

    public Server server() {
        ForzaProperties.Server ps = properties.getServer();
        ps.setEnabled(true);

        ForzaServer server = new ForzaServer();
        server.option(ForzaServerOption.PORT, ps.getPort())
                .option(ForzaGenericOption.TCP_NODELAY, properties.isTcpNodelay())
                .option(ForzaGenericOption.TCP_SO_REUSEADDR, properties.isTcpSoReuseaddr())
                .option(ForzaGenericOption.NETTY_BUFFER_HIGH_WATER_MARK, properties.getNettyBufferHighWatermark())
                .option(ForzaGenericOption.NETTY_BUFFER_LOW_WATER_MARK, properties.getNettyBufferLowerWatermark())
                .option(ForzaGenericOption.NETTY_IO_RATIO, properties.getNettyIORatio())
                .option(ForzaGenericOption.TCP_SO_KEEPALIVE, properties.isTcpSoKeepalive())
                .option(ForzaGenericOption.IO_THREADS, properties.getNettyServerIoThread())
                .option(ForzaRemotingOption.SERIALIZATION, properties.getSerialization());
        server.startUp();
        return server;
    }
}
