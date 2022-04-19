package org.forza.sample;

import org.forza.transport.Client;
import com.forza.sample.api.SimpleRequestBody;
import com.forza.sample.api.SimpleResponseBody;
import org.forza.autoconfigure.EnableForza;
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

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private Client client;

    public static void main(String[] args) {
        SpringApplication.run(ForzaClientApplication.class, args);
    }

    @GetMapping("/test")
    public Object rest() {
        SimpleRequestBody requestBody = new SimpleRequestBody("criss", 25, 17731352346L);
        SimpleResponseBody responseBody = client.request(requestBody);
        logger.info("send data : {}, receive data : {}", requestBody.toString(), responseBody.toString());
        return Result.success(responseBody.toString());
    }
}
