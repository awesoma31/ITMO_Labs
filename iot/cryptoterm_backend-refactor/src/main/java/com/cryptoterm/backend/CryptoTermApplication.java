package com.cryptoterm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoTermApplication {
    public static void main(String[] args) {
        SpringApplication.run(CryptoTermApplication.class, args);
    }
}


