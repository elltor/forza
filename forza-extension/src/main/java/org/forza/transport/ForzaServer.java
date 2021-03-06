package org.forza.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.forza.codec.CodecAdapter;
import org.forza.codec.ForzaCodec;
import org.forza.common.Url;
import org.forza.config.ForzaGenericOption;
import org.forza.config.ForzaServerOption;
import org.forza.protocol.ForzaProtocol;
import org.forza.util.NetUtils;
import org.forza.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ForzaServer extends AbstractServer<ForzaServer> {
    private static final Logger logger = LoggerFactory.getLogger(ForzaServer.class);
    private ServerBootstrap bootstrap;
    // 主线程 - 接受请求&建立连接
    private EventLoopGroup bossGroup;
    // 工作线程 - RPC处理
    private EventLoopGroup workerGroup;
    private volatile Channel channel;

    public ForzaServer() {
        super(new ForzaCodec(), new ForzaProtocol());
    }


    @Override
    public void doOpen() throws Throwable {
        bootstrap = new ServerBootstrap();
        // accept worker
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("BoltServerBoss", false));
        // read/write worker
        workerGroup = new NioEventLoopGroup(this.option(ForzaGenericOption.IO_THREADS),
                new DefaultThreadFactory("BoltServerWorker", true));
        port = this.option(ForzaServerOption.PORT);
        initUrl(new Url(NetUtils.getLocalHost(), port));
        // 创建handler
        final ForzaHandler serverHandler = new ForzaHandler(getUrl(), getProtocol(), isServerSide());
        int idleTimeout = UrlUtils.getIdleTimeout(getUrl());
        // 添加handler监听
        serverHandler.setConnectionEventListener(getConnectionEventListener());

        // 配置Netty
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // TCP参数配置
                .childOption(ChannelOption.TCP_NODELAY, this.option(ForzaGenericOption.TCP_NODELAY))
                .childOption(ChannelOption.SO_REUSEADDR, this.option(ForzaGenericOption.TCP_SO_REUSEADDR))
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, initWriteBufferWaterMark())
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        CodecAdapter adapter = new CodecAdapter(getCodec(), getUrl());
                        ch.pipeline()
                                // name - component
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, idleTimeout, MILLISECONDS))
                                // Handler adds last
                                .addLast(serverHandler);
                    }
                });
        // 启动server并阻塞
        ChannelFuture future = bootstrap.bind(this.port).syncUninterruptibly();
        channel = future.channel();
    }

    @Override
    protected void doClose() throws Throwable {
        try {
            if (channel != null) {
                // unbind.
                channel.close();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            if (bootstrap != null) {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }
}
