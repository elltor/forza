package org.forza.transport;

import org.forza.codec.Codec;
import org.forza.common.exception.RemotingException;
import org.forza.protocol.Protocol;
import org.forza.util.ExecutorUtil;
import org.forza.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 抽象服务器端类
 */
public abstract class AbstractServer<T extends AbstractServer> extends AbstractEndpoint implements Server {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);

    protected int port;

    public AbstractServer(Codec codec, Protocol protocol) {
        super(true, codec, protocol);
    }


    @Override
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(NetUtils.getLocalHost(), port);
    }

    @Override
    public void startUp() {
        super.startUp();
        long start = System.currentTimeMillis();
        try {
            doOpen();
            if (logger.isInfoEnabled()) {
                logger.info("Start " + getClass().getSimpleName() + " bind port [{}], start time {}ms",
                        port, System.currentTimeMillis() - start);
            }
        } catch (Throwable t) {
            shutDown();
            throw new RemotingException(getLocalAddress(), null, "Failed to bind " + getClass().getSimpleName()
                    + " on " + getLocalAddress() + ", cause: " + t.getMessage(), t);
        }

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
    public void close() {
        if (logger.isInfoEnabled()) {
            logger.info("Close " + getClass().getSimpleName() + " bind " + getLocalAddress());
        }
        ExecutorUtil.gracefulShutdown(getProtocol().getDefaultExecutor(), 100);

        try {
            doClose();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }
}
