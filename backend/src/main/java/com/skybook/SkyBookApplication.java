package com.skybook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * SkyBook AI Backend — security research &amp; training lab.
 * This application is NOT production-secure.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class SkyBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyBookApplication.class, args);
    }
}
