package com.campus.paygate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //(exclude = { SecurityAutoConfiguration.class })
public class PaygateApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaygateApplication.class, args);
    }
}