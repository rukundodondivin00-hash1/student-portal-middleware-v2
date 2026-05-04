package com.auca.studentportal.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, AucaApiProperties props) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(props.getConnectTimeoutSeconds()))
                .setReadTimeout(Duration.ofSeconds(props.getReadTimeoutSeconds()))
                .build();
    }
}
