package com.coffee_is_essential.iot_cloud_ota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IotCloudOtaApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotCloudOtaApplication.class, args);
    }
}

