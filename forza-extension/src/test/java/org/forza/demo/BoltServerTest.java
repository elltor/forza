package org.forza.demo;

import org.forza.common.enums.ConnectionEventType;
import org.forza.config.ForzaServerOption;
import org.forza.transport.ForzaServer;

public class BoltServerTest {
    public static void main(String[] args) {
        ForzaServer server = new ForzaServer();
        server.option(ForzaServerOption.PORT,9091);
        server.addConnectionEventProcessor(ConnectionEventType.CONNECT,((connection) -> {
            // 并发控制，连接统计等
        }));
        server.startUp();
    }
}
