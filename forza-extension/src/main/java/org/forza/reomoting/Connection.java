package org.forza.reomoting;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.forza.common.Url;
import org.forza.common.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Connection extends AbstractConnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(Connection.class);

    /** 心跳连数 */
    public static final AttributeKey<Integer> HEARTBEAT_COUNT = AttributeKey.valueOf("heartbeatCount");

    /** 通道 - 连接 */
    public static final ConcurrentMap<io.netty.channel.Channel, Connection> connectionMap = new ConcurrentHashMap<io.netty.channel.Channel, Connection>();

    /** 该连接的通道 */
    private Channel channel;

    private Url url;

    public Connection(Channel channel) {
        this(channel, null);
    }

    public Connection(Channel channel, Url url) {
        this.channel = channel;
        this.url = url;
        attr(HEARTBEAT_COUNT).set(0);
    }

    public Channel getChannel() {
        return this.channel;
    }

    public Url getUrl() {
        return this.url;
    }

    public boolean isWritable() {
        return this.channel.isWritable();
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) throws RemotingException {
        if (channel == null) {
            throw new RemotingException(this, "The connection has no valid channel, Connection url" + this.url);
        }

        if (!isWritable()) {
            logger.error("The connection {} write overflow !!!", this);
            throw new RemotingException(this, "The connection {} write overflow !!!" + this);
        }
        return channel.writeAndFlush(msg);
    }

    public boolean isActive(){
        return channel.isActive();
    }

    public void close() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Close connection" + channel);
            }
            try {
                channel.close();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }

            try {
                removeChannelIfDisconnected(channel);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public InetSocketAddress getLocalAddress() {
        if (channel == null) {
            return null;
        }
        return (InetSocketAddress) this.channel.localAddress();
    }

    public InetSocketAddress getRemoteAddress() {
        if (channel == null) {
            return null;
        }

        return (InetSocketAddress) this.channel.remoteAddress();
    }

    @Override
    public Connection getConnection() {
        return this;
    }

    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return channel.attr(key);
    }

    public void resetHeartbeat() {
        attr(Connection.HEARTBEAT_COUNT).set(0);
    }

    public static Connection getOrAddConnection(Channel ch, Url url) {
        if (ch == null) {
            return null;
        }
        Connection ret = connectionMap.get(ch);
        if (ret == null) {
            Connection connection = new Connection(ch, url);
            if (ch.isActive()) {
                ret = connectionMap.putIfAbsent(ch, connection);
            }
            if (ret == null) {
                ret = connection;
            }
        }
        return ret;
    }

    public static Connection getConnection(Channel ch) {
        if (ch == null) {
            return null;
        }
        return connectionMap.get(ch);
    }

    public static void removeChannelIfDisconnected(io.netty.channel.Channel ch) {
        if (ch != null && !ch.isActive()) {
            connectionMap.remove(ch);
        }
    }

    public static void clear() {
        try {
            for (Map.Entry<io.netty.channel.Channel, Connection> entry : connectionMap.entrySet()) {
                io.netty.channel.Channel ch = entry.getKey();
                removeChannelIfDisconnected(ch);
            }
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Connection other = (Connection) obj;
        if (channel == null) {
            if (other.channel != null) {
                return false;
            }
        } else if (!channel.equals(other.channel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[connection=" + channel + "]";
    }
}
