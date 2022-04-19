package org.forza.coonection.pool;

import org.forza.coonection.SimpleHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;

import java.net.InetSocketAddress;

/**
 * channel 线程池。
 * map : Inet -> ChannelPool
 */
public class SimpleChannelPoolMap extends AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool> {
    private Bootstrap bootstrap;
    SimpleHandler simpleHandler = new SimpleHandler();

    public SimpleChannelPoolMap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected SimpleChannelPool newPool(InetSocketAddress key) {
        return new SimpleChannelPool(bootstrap.remoteAddress(key), new ChannelPoolHandler() {
            @Override
            public void channelReleased(Channel ch) throws Exception {
                System.out.println("channelReleased: " + ch);
            }

            @Override
            public void channelAcquired(Channel ch) throws Exception {
                System.out.println("channelAcquired: " + ch);

            }
            @Override
            public void channelCreated(Channel ch) throws Exception {
                // 为channel添加handler
                ch.pipeline().addLast(simpleHandler);
            }
        });
    }
}
