package org.forza.sample;

import com.forza.sample.api.GoodsRequestBody;
import com.forza.sample.api.GoodsResponseBody;
import org.forza.transport.Client;
import com.forza.sample.api.SimpleRequestBody;
import com.forza.sample.api.SimpleResponseBody;
import org.forza.autoconfigure.EnableForza;
import org.openjdk.jmh.annotations.Benchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@SpringBootApplication
@EnableForza
@RestController
public class ForzaClientApplication {

    private Logger logger = LoggerFactory. getLogger(getClass());

    @Resource
    private Client client;

    public static void main(String[] args) {
        SpringApplication.run(ForzaClientApplication.class, args);
    }

    @GetMapping("/test1")
    public Object userCallTest() {
        SimpleRequestBody requestBody = new SimpleRequestBody("criss", 25, 17731352346L);
        SimpleResponseBody responseBody = client.request(requestBody);
        logger.info("send data : {}, receive data : {}", requestBody.toString(), responseBody.toString());
        return Result.success(responseBody.toString());
    }

    @GetMapping("/test2")
    public Object goodsQueryTest() {
        GoodsRequestBody goodsRequestBody = new GoodsRequestBody("卫衣");
        GoodsResponseBody goodsResponseBody = client.request(goodsRequestBody);
        logger.info("send data : {}, receive data : {}", goodsRequestBody.toString(), goodsResponseBody.toString());
        return Result.success(goodsResponseBody.toString());
    }

    @GetMapping("/test3")
    public Object test3() {
        return Result.success("OK");
    }

    @GetMapping("/test4")
    public Object test4() {
        return Result.success("OK");
    }

    @GetMapping("/")
    public Object test5() {
        return Result.success("/test1,/test2,/test3,/test4...");
    }

}
