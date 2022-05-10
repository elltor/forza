package org.forza.demo;

import org.forza.common.Url;
import org.forza.config.ForzaClientOption;
import org.forza.protocol.ReqBody;
import org.forza.transport.ForzaClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@RunWith(JUnit4.class)
public class ForzaClientTest {
    private static final Logger logger = LoggerFactory.getLogger(ForzaClientTest.class);
    ForzaClient client;

    @Before
    public void setUp() {
        client = new ForzaClient();
        client.option(ForzaClientOption.CONNECT_TIMEOUT, 3000);
        client.startUp();
    }

    @Test
    public void sync_test() {
        Map<String, Object> map = new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT, 9000);
        Url url = Url.builder()
                .host("127.0.0.1")
                .port(9091)
                .setParameters(map)
                .build();
        ReqBody requestBody = new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        String body = client.request(url, requestBody);
        logger.info("Client Recv : " + body);
    }


    @Test
    public void async_test() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT, 9000);
        // 异步调用
        map.put(Url.ASYNC, true);
        Url url = Url.builder()
                .host("127.0.0.1")
                .port(9091)
                .setParameters(map)
                .build();
        ReqBody requestBody = new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        CompletableFuture<String> future = client.request(url, requestBody);
        logger.info("Client Recv : " + future.get());
    }

    @Test
    public void call_back() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT, 9000);
        // 异步调用
        map.put(Url.ASYNC, true);
        Url url = Url.builder()
                .host("127.0.0.1")
                .port(9091)
                .setParameters(map)
                .build();
        ReqBody requestBody = new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        CompletableFuture<String> future = client.request(url, requestBody);
        CountDownLatch latch = new CountDownLatch(1);
        future.whenComplete((res, cause) -> {
            if (cause != null) {
                // 异常处理
            }
            latch.countDown();
            logger.info("Client Recv : " + res);
        });
        latch.await();
    }

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


}
