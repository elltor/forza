package org.forza.transport;

import io.netty.channel.WriteBufferWaterMark;
import org.forza.codec.Codec;
import org.forza.common.Url;
import org.forza.common.enums.ConnectionEventType;
import org.forza.config.*;
import org.forza.protocol.Protocol;
import org.forza.reomoting.AbstractLifeCycle;
import org.forza.reomoting.ConnectionEventListener;
import org.forza.reomoting.ConnectionEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 抽象终端实现类
 */
public abstract class AbstractEndpoint extends AbstractLifeCycle implements Endpoint, Configurable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractEndpoint.class);

    /**
     * 终端选项
     */
    protected final ForzaOptions options = new ForzaOptions();

    /**
     * 编解码器
     */
    private Codec codec;

    /**
     * 协议
     */
    private Protocol protocol;

    private volatile Url url;

    private boolean serverSide;

    /** 连接事件监听器 */
    private ConnectionEventListener connectionEventListener = new ConnectionEventListener();

    /**
     * 启动
     *
     * @throws Throwable
     */
    protected abstract void doOpen() throws Throwable;

    /**
     * 关闭
     *
     * @throws Throwable
     */
    protected abstract void doClose() throws Throwable;

    public AbstractEndpoint(Codec codec, Protocol protocol) {
        this(false, codec, protocol);
    }

    public AbstractEndpoint(boolean serverSide, Codec codec, Protocol protocol) {
        this.serverSide = serverSide;
        this.codec = codec;
        this.protocol = protocol;
    }

    @Override
    public <T> Configurable option(ForzaOption<T> option, T value) {
        options.option(option, value);
        return this;
    }

    @Override
    public <T> T option(ForzaOption<T> option) {
        return options.option(option);
    }

    protected Map<String, Object> options(Class<? extends ForzaOption> type) {
        return options.options(type);
    }

    protected Codec getCodec() {
        return codec;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    protected void init(Url url) {
        url.addParameters(options(ForzaRemotingOption.class));
        if (isServerSide()) {
            url.addParameters(options(ForzaServerOption.class));
        } else {
            if (url.getHost() == null) {
                url.setHost(option(ForzaClientOption.HOST));
                url.setPort(option(ForzaClientOption.PORT));
            }
            url.addParameters(options(ForzaClientOption.class));
        }
        this.url = url;
    }

    @Override
    public Url getUrl() {
        return url;
    }

    public WriteBufferWaterMark initWriteBufferWaterMark() {
        Integer lowWaterMark = this.option(ForzaGenericOption.NETTY_BUFFER_LOW_WATER_MARK);
        Integer highWaterMark = this.option(ForzaGenericOption.NETTY_BUFFER_HIGH_WATER_MARK);
        String prefix = isServerSide() ? "[bolt server side]" : "[bolt client side]";
        if (lowWaterMark > highWaterMark) {
            throw new IllegalArgumentException(String.format(prefix + " netty high water mark {%s}" + " should not be smaller than low water mark {%s} bytes)", highWaterMark, lowWaterMark));
        } else {
            logger.info(prefix + " netty low water mark is {} bytes, high water mark is {} bytes", lowWaterMark, highWaterMark);
        }
        return new WriteBufferWaterMark(lowWaterMark, highWaterMark);
    }

    @Override
    public boolean isServerSide() {
        return serverSide;
    }

    public void addConnectionEventProcessor(ConnectionEventType type, ConnectionEventProcessor processor) {
        connectionEventListener.addConnectionEventProcessor(type, processor);
    }

    protected ConnectionEventListener getConnectionEventListener() {
        return connectionEventListener;
    }
}
