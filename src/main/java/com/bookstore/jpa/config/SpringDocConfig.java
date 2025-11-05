package com.bookstore.jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SpringDocConfig {
     @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("BookStore - Sistema de gerenciamento de livros")
                        .description("API REST para gerenciamento livros.")
                        .version("v1.0.0"));
    }
}

