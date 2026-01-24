package com.example.demo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
            .info(
                new Info()
                    .title("Todo API")
                    .description("这是一个简单的待办事项管理API，提供CRUD功能")
                    .version("v1.0")
                    .contact(new Contact()
                        .name("API Support")
                        .email("support@example.com"))
            );
    }
}