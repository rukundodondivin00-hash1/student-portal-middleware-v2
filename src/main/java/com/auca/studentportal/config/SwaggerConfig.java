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
                                1. Call POST /api/v1/middleware/auth/signin with your AUCA credentials
                                2. Copy the value of the `access_token` cookie from the response
                                3. Paste it in the `Cookie` header field below as: `access_token=<value>`
                                """)
                        .contact(new Contact()
                                .name("AUCA Student Portal Team")));
    }

    /**
     * Adds a Cookie header parameter to every student-facing endpoint
     * so testers can paste their session cookie directly in Swagger UI.
     */
    @Bean
    public OperationCustomizer cookieHeaderCustomizer() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.getBeanType().getName().contains("StudentPaymentController")) {
                operation.addParametersItem(new Parameter()
                        .in("header")
                        .name("Cookie")
                        .description("Paste your AUCA session cookies here. Example: access_token=your-token-value")
                        .required(false)
                        .schema(new StringSchema()));
            }
            return operation;
        };
    }
}
