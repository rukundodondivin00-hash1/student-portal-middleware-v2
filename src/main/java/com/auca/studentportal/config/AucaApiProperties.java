package com.auca.studentportal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auca.api")
public class AucaApiProperties {
    private String baseUrl;
    private String apiKey;
    private int connectTimeoutSeconds = 10;
    private int readTimeoutSeconds = 30;
}
