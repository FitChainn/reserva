package com.fitchain.reserva.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Reserva API - FitChain")
                        .description("API DE GESTIÓN DE LAS RESERVAS")
                        .version("1.0.0")).servers(List.of(
                        new Server().url("http://44.197.145.9:8087").description("AWS"),
                        new Server().url("http://localhost:8087").description("Local")
                ));
    }
}