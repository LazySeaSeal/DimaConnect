package com.recrutement.app.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API DimaConnect",
        version = "1.0",
        description = "API pour la gestion de recrutement"
    )
)
public class SwaggerConfig {
    // Aucun code supplémentaire nécessaire
}