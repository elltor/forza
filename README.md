# Forza

Forza 是一款基于 Netty 的可以支持百万级别的并发连接的高性能、高度可扩展的的网络通讯框架，它参考了 Dubbo 和 SOFABolt 的网络通讯模块的设计，Forza 可以使用在IM、长连接等领域，它具有以下的特性：

- 私有的通讯协议
    - 可定制的编/解码器
    - 支持多种序列化机制
    - CRC校验
- 客户端/服务端连接管理
    - 无锁建连
    - 连接的心跳和空闲检测
    - 客户端连接池
    - 自动断连和重连
    - 高效和可定制化的IO模型
- 丰富的通信模型
    - oneway
    - sync
    - callback
    - future
- 支持客户端/服务端异步化编程
- 超时控制
- 使用 SPI 扩展点加载，扩展性强
- 鉴权

oneway：不关注结果，即客户端发起调用后不关注服务端返回的结果，适用于发起调用的一方不需要拿到请求的处理结果，或者说请求或处理结果可以丢失的场景；
sync：同步调用，调用线程会被阻塞，直到拿到响应结果或者超时，是最常用的方式，适用于发起调用方需要同步等待响应的场景；
future：异步调用，调用线程不会被阻塞，通过 future 获取调用结果时才会被阻塞，适用于需要并发调用的场景，比如调用多个服务端并等待所有结果返回后执行特定逻辑的场景；
callback：异步调用，调用线程不会被阻塞，调用结果在 callback 线程中被处理，适用于高并发要求的场景；


## 压力测试

物理环境：

- MacOS
- CPU: 4核 2.4GHz Intel Core i5
- 内存：8g 可用内存2g

以下为同步调用压力测试结果，由于实际限制只能简单进行压测。

![](https://user-gold-cdn.xitu.io/2020/5/8/171f3e97adb5caa4?w=1850&h=1342&f=png&s=231492)

## 1.使用方式

- 可以直接无缝结合 SpringBoot 使用，使用示例请参考 forza-example 项目


- 同步调用

```java
    @Before
public void setUp(){
        client=new ForzaClient();
        client.option(ForzaClientOption.CONNECT_TIMEOUT,3000);
        client.startUp();
        }

@Test
public void sync_test(){
        Map<String, Object> map=new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT,9000);
        Url url=Url.builder()
        .host("127.0.0.1")
        .port(9091)
        .setParameters(map)
        .build();
        ReqBody requestBody=new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        String body=client.request(url,requestBody);
        logger.info("Client Recv : "+body);
        }    
```

- 异步调用 Futrue

```java
    @Test
public void async_test()throws Exception{
        Map<String, Object> map=new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT,9000);
        // 异步调用
        map.put(Url.ASYNC,true);
        Url url=Url.builder()
        .host("127.0.0.1")
        .port(9091)
        .setParameters(map)
        .build();
        ReqBody requestBody=new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        CompletableFuture<String> future=client.request(url,requestBody);
        logger.info("Client Recv : "+future.get());
        }
```

- callBack调用

```java
    @Test
public void call_back()throws Exception{
        Map<String, Object> map=new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT,9000);
        // 异步调用
        map.put(Url.ASYNC,true);
        Url url=Url.builder()
        .host("127.0.0.1")
        .port(9091)
        .setParameters(map)
        .build();
        ReqBody requestBody=new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        CompletableFuture<String> future=client.request(url,requestBody);
        CountDownLatch latch=new CountDownLatch(1);
        future.whenComplete((res,cause)->{
        if(cause!=null){
        // 异常处理
        }
        latch.countDown();
        logger.info("Client Recv : "+res);
        });
        latch.await();
        }

```

- 单向调用 oneway

```java
@Test
    public void oneway_test() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT, 9000);
        // 单向调用
        map.put(Url.ONEWAY, true);
        Url url = Url.builder()
                .host("127.0.0.1")
                .port(9091)
                .setParameters(map)
                .build();
        ReqBody requestBody = new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        client.request(url, requestBody);
    }
```

ReqBody 实现 Serializable 接口

- 命令处理器CommandHandler

默认提供了 GeneralCmdHandler 处理通用命令，如果你的请求没有指定命令将会默认被 GeneralCmdHandler 处理。你只需要注册 UserProcesser 即可。注册后在 `META-INF/services/`
文件夹配置扩展点实现。

```java
public class SimpleReqProcesser extends AbstractUserProcessorAdapter<ReqBody> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    // 处理的数据类型
    @Override
    public String interest() {
        return ReqBody.class.getName();
    }


    @Override
    public String handleRequest(ReqBody body) throws Exception {
        logger.error("handleRequest: " + body.toString());

        return "server success";
    }

    //是否在IO线程序列化body和处理业务
    @Override
    public boolean processInIOThread() {
        return false;
    }
}
```

- 扩展 CommandHandler

1. 继承 `AbstractCommandHandler` 复写 handleRequest 和 handleResponse 方法处理你的命令。
2. 在 `META-INF/services/` 文件夹配置扩展点实现。

你可以参考 `HeartbeatHandler` 和 `GeneralCmdHandler`

## 2.源码设计

### 2.1 架构设计

![](https://user-gold-cdn.xitu.io/2020/5/6/171e99cfdb038e65?w=1918&h=1016&f=png&s=163835)

![](https://user-gold-cdn.xitu.io/2020/5/7/171ecfd3ae2d6685?w=1186&h=1120&f=png&s=166445)

### 2.2 协议设计

![forza-extension协议](https://user-gold-cdn.xitu.io/2020/4/29/171c4e0622ead6bf?w=1792&h=282&f=png&s=43871)

1. 第1个字节是魔数，对于非本协议的包可以进行快速检测(fast-fail)，不需要解码后的处理同时保证安全性。
2. 第9个bit是 requst/response 标志，1表示 requst。
3. 第10个bit表示是单向调用还是双向调用。
4. 第11个bit表示是否示心跳包。
5. 第2个字节的剩余的5个bit代表序列化ID，目前只支持hessian序列化。
6. 第3-4字节(short)表示commnd code，表示这个包的命令类型。
7. 第5个字节表示响应的状态，以便客户端快速识别异常。
8. 第6个字节表示标志位(flags)，用来标示是否开启CRC冗余校验、数据压缩等功能。
9. 第7-10字节(int)表示请求的唯一ID，用于双向通信的时候唤醒阻塞的线程。
10. 第11-14字节(int)表示数据body的长度，用于解码。

#### 2.2.1 协议命令

![](https://user-gold-cdn.xitu.io/2020/5/7/171edd58245f8445?w=1070&h=540&f=png&s=32592)

#### 2.2.2 命令处理器设计

![](https://user-gold-cdn.xitu.io/2020/5/7/171ee1b098b287a6?w=2012&h=988&f=png&s=211148)

### 2.3 编解码

由于TCP的沾包和拆包问题，一般来说编解码分为以下几种方式：

- 基于分割符的协议
- 基于定长的协议
- 基于变长的协议

一般来说不管使用何种的方式，编解码器都需要继承 MessageToByteEncoder 和 ByteToMessageDecoder 两个类。需要注意的是因为 ByteToMessageDecoder 维护了有状态的 BtyeBuf 累加器所有解码器是有状态的，不能使用 `@ChannelHandler.Sharable`

在 sofa-bolt 的基础上 forza-extension 参考了 Dubbo，增加了客户端和服务端的负载数据大小的校验，可以实现大包的快速失败（fast-fail）。

### 2.4 连接管理

基于 Netty  的 FixedChannelPool 实现客户端连接池和客户端并发控制。

服务端并发控制你可以使用连接监听器实现

```
        ForzaServer server = new ForzaServer();
        server.option(ForzaServerOption.PORT,9091);
        server.addConnectionEventProcessor(ConnectionEventType.CONNECT,((connection) -> {
            // 并发控制，连接统计等
        }));
        server.startUp();
```

[FixedChannelPool原理](https://juejin.im/post/5e9942e2f265da47d00a6776)

sofa-forza 的客户端连接池无锁建立连接的原理

1. 使用的是 ConcurrentHashMap 的 putIfAbsent 保证无锁建立连接池
2. 使用 FutureTask 的在并发环境下 Callable 只执行一次的特新解决并发问题

### 2.5 空闲检测和心跳

空闲检测基于 Netty 的 IdleStateEvent 进行读写的空闲检测。

- 客户端：只检测读超时，默认15秒发送一次心跳。超过3次没有收到响应，就会关闭连接并进行重连
- 服务端：检测读写超时，默认90内没有读写，则直接关闭连接，等待重连。

### 2.6 重连

在 sofa-bolt 的基础上，使用 `HashedWheelTimer` 实现了重连时间的指数退避操作，客户端三次更新读时间戳则立即重连，第二次重连为3s，第三次6秒，依次类推。默认尝试6次。

## Netty最佳实践

1. 选择最合适的 Reactor 模型，一般来说服务端的 accept 线程为1即可，worker 线程一般为 cpu*2
2. 保证串行化处理IO事件和业务，避免加锁操作。
    - ConcurrentHashMap 的 putIfAbsent（本质是CAS）。
    - 利用 EventLoop 执行可以保证串行化执行任务，避免了线程上下文切换。
    - Futuretask 和 Callable 在多线程下的特性。
3. IO密集型的任务可以使直接在IO线程处理（EventLoop线程）避免线程上下文切换的耗时，CPU密集型任务，应当IO线程和业务线程隔离，释放IO线程进行 read/write。灵活配置线程很重要。
4. 对于无状态的 ChannelHandler 应当设置为共享模式 `@ChannelHandler.Sharable`，避免生成太多对象。 5. ChannelHandlerContext 的 ctx.write() 与 ctx.channel()
   .write() 方法。前者只会处理当前 Handler 前面的 Handler，后者会从 tail 节点开始，处理整个 ChannelPipeline。

6. 在写数据的时候应当使用 isWritable() 方法来判断一下当前 ChannelOutboundBuffer
   里的写缓存水位。因为 writeAndFlush 是先发送到 ChannelOutboundBuffer 缓冲区，如果接受方窗口一直很小，或者网络拥塞很可能会导致OOM发生。

7. 能使用数组的情况不要使用散列表（Map）

8. 超时控制可以使用 HashedWheelTimer。

