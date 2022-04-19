package org.forza.transport;

import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import org.forza.codec.Codec;
import org.forza.common.Constants;
import org.forza.common.Url;
import org.forza.common.command.CommandFactory;
import org.forza.common.command.RequestCommand;
import org.forza.common.exception.RemotingException;
import org.forza.config.ForzaClientOption;
import org.forza.protocol.Protocol;
import org.forza.reomoting.Connection;
import org.forza.reomoting.FutureAdapter;
import org.forza.util.ExecutorUtil;
import org.forza.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * @Author:  
 * @DateTime: 2020/4/22
 * @Description: TODO
 */
public abstract class AbstractClient<K, P extends ChannelPool>
        extends AbstractEndpoint
        implements Client, ChannelPoolMap<K, P>, Iterable<Map.Entry<K, P>>, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);
    protected ReconnectClient reconnectClient;
    private final ConcurrentMap<K, P> map = PlatformDependent.newConcurrentHashMap();
    private CommandFactory commandFactory;

    public AbstractClient(Codec codec, Protocol protocol) {
        super(codec, protocol);
        this.commandFactory = new CommandFactory();
        this.reconnectClient = new ReconnectClient(this);
        this.reconnectClient.startUp();
    }

    public AbstractClient(boolean serverSide, Codec codec, Protocol protocol) {
        super(serverSide, codec, protocol);
    }

    @Override
    public final P get(K key) {
        P pool = map.get(checkNotNull(key, "key"));
        if (pool == null) {
            pool = newPool(key);
            P old = map.putIfAbsent(key, pool);
            if (old != null) {
                // We need to destroy the newly created pool as we not use it.
                poolCloseAsyncIfSupported(pool);
                pool = old;
            }
        }
        return pool;
    }

    /**
     * Remove the {@link ChannelPool} from this {@link AbstractChannelPoolMap}. Returns {@code true} if removed,
     * {@code false} otherwise.
     * <p>
     * If the removed pool extends {@link SimpleChannelPool} it will be closed asynchronously to avoid blocking in
     * this method.
     * <p>
     * Please note that {@code null} keys are not allowed.
     */
    public final boolean remove(K key) {
        P pool = map.remove(checkNotNull(key, "key"));
        if (pool != null) {
            poolCloseAsyncIfSupported(pool);
            return true;
        }
        return false;
    }

    /**
     * Remove the {@link ChannelPool} from this {@link AbstractChannelPoolMap}. Returns a future that comletes with a
     * {@code true} result if the pool has been removed by this call, otherwise the result is {@code false}.
     * <p>
     * If the removed pool extends {@link SimpleChannelPool} it will be closed asynchronously to avoid blocking in
     * this method. The returned future will be completed once this asynchronous pool close operation completes.
     */
    private Future<Boolean> removeAsyncIfSupported(K key) {
        P pool = map.remove(checkNotNull(key, "key"));
        if (pool != null) {
            final Promise<Boolean> removePromise = GlobalEventExecutor.INSTANCE.newPromise();
            poolCloseAsyncIfSupported(pool).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        removePromise.setSuccess(Boolean.TRUE);
                    } else {
                        removePromise.setFailure(future.cause());
                    }
                }
            });
            return removePromise;
        }
        return GlobalEventExecutor.INSTANCE.newSucceededFuture(Boolean.FALSE);
    }

    /**
     * If the pool implementation supports asynchronous close, then use it to avoid a blocking close call in case
     * the ChannelPoolMap operations are called from an EventLoop.
     *
     * @param pool the ChannelPool to be closed
     */
    private static Future<Void> poolCloseAsyncIfSupported(ChannelPool pool) {
        if (pool instanceof SimpleChannelPool) {
            return ((SimpleChannelPool) pool).closeAsync();
        } else {
            try {
                pool.close();
                return GlobalEventExecutor.INSTANCE.newSucceededFuture(null);
            } catch (Exception e) {
                return GlobalEventExecutor.INSTANCE.newFailedFuture(e);
            }
        }
    }

    @Override
    public final Iterator<Map.Entry<K, P>> iterator() {
        return new ReadOnlyIterator<Map.Entry<K, P>>(map.entrySet().iterator());
    }

    /**
     * Returns the number of {@link ChannelPool}s currently in this {@link AbstractChannelPoolMap}.
     */
    public final int size() {
        return map.size();
    }

    /**
     * Returns {@code true} if the {@link AbstractChannelPoolMap} is empty, otherwise {@code false}.
     */
    public final boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public final boolean contains(K key) {
        return map.containsKey(checkNotNull(key, "key"));
    }

    @Override
    public final void close() {
        // 关闭连接池
        for (K key : map.keySet()) {
            // Wait for remove to finish to ensure that resources are released before returning from close
            removeAsyncIfSupported(key).syncUninterruptibly();
        }
        // 清空连接
        Connection.clear();
        // 关闭线程池
        ExecutorUtil.gracefulShutdown(getProtocol().getDefaultExecutor(), 100);
        try {
            doClose();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public void shutDown() {
        super.shutDown();
        try {
            close();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    @Override
    public void startUp() {
        super.startUp();

        try {
            doOpen();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    protected int getConnectTimeout() {
        Integer connectTimeout = Constants.DEFAULT_CONNECT_TIMEOUT;
        if (getUrl() != null) {
            connectTimeout = getUrl().getParameter(Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT);
            if (connectTimeout < Constants.DEFAULT_CONNECT_TIMEOUT) {
                connectTimeout = Constants.DEFAULT_CONNECT_TIMEOUT;
            }
        }
        return connectTimeout;
    }


    @Override
    public <T> T request(Object request) throws RemotingException {
        Url url = Url.builder().host(option(ForzaClientOption.HOST))
                .port(option(ForzaClientOption.PORT))
                .build();
        return request(url, request);
    }

    @Override
    public <T> T request(Url url, Object msg) throws RemotingException {
        init(url);
        Connection connection = ctreateConnectionIfAbsent(url);
        RequestCommand request = commandFactory.createRequest(msg);
        if (UrlUtils.isOneway(url)) {
            connection.writeAndFlush(request);
            return null;
        }
        int timeout = UrlUtils.getTimeout(url, option(ForzaClientOption.TIMEOUT));
        FutureAdapter<Object> future = connection.send(request, timeout);
        if (UrlUtils.isAsync(url)) {
            return (T) future;
        } else {
            try {
                return (T) future.get();
            } catch (Exception e) {
                throw new RemotingException(connection, e);
            }
        }
    }

    public InetSocketAddress getConnectAddresss() {
        return new InetSocketAddress(getUrl().getHost(), getUrl().getPort());
    }

    /**
     * Called once a new {@link ChannelPool} needs to be created as non exists yet for the {@code key}.
     */
    protected abstract P newPool(K key);

}
