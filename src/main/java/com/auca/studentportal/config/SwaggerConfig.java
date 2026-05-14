package com.auca.studentportal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Portal Middleware API")
                        .version("1.0.0")
                        .description("""
                                Finance–Student Portal Integration Middleware.
                                
                                This service acts as a secure bridge between the Student Portal
                                and the AUCA Finance system (https://auca-ims.onrender.com).
                                
                                **How to authenticate in Swagger:**
                                1. Sign into AUCA student portal in your browser
                                2. Copy the access_token cookie value
                                3. Paste it in the Cookie field as: access_token=<token>
                                
                                **Alternative:**
                                Use the Authorization header with Bearer token
                                """)
                        .contact(new Contact()
                                .name("AUCA Student Portal Team")));
    }

    /**
     * Adds Authorization and Cookie header parameters to every student-facing endpoint.
     * Authorization header works in Swagger UI, Cookie header works with curl.
     */
    @Bean
    public OperationCustomizer cookieHeaderCustomizer() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.getBeanType().getName().contains("StudentPaymentController")) {
                operation.addParametersItem(new Parameter()
                        .in("header")
                        .name("Authorization")
                        .description("Bearer token for authentication (works in Swagger UI). Example: Bearer eyJ...")
                        .required(false)
                        .schema(new StringSchema()));
                operation.addParametersItem(new Parameter()
                        .in("header")
                        .name("Cookie")
                        .description("Cookie header for curl/testing. Example: access_token=eyJ...")
                        .required(false)
                        .schema(new StringSchema()));
            }
            return operation;
        };
    }
}

