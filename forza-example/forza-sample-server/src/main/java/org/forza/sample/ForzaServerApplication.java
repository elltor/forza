package org.forza.sample;

import org.forza.autoconfigure.EnableForza;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableForza
public class ForzaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForzaServerApplication.class, args);
    }

}
