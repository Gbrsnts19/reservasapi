package br.org.fadesp.reservasapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reservasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Reservas - Coworking")
                        .description("API REST para gerenciamento de salas e reservas")
                        .version("1.0.0"));
    }
}
