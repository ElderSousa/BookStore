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
                .info(new Info().title("MS-userApi - Sistema de gerencimento de usuários")
                        .description("API REST para gerenciamento usuários.")
                        .version("v1.0.0"));
    }
}

