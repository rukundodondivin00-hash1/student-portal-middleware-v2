package com.auca.studentportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.auca.studentportal.config.AucaApiProperties;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties(AucaApiProperties.class)
public class StudentPortalMiddlewareApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentPortalMiddlewareApplication.class, args);
    }
}
