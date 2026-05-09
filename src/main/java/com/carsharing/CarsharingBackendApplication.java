package com.carsharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarsharingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarsharingBackendApplication.class, args);
    }
}