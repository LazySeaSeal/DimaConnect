package com.recrutement.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Recrutement - Section Candidat")
                        .description("API REST pour la gestion des candidats, profils, compétences, abonnements et recherche d'emploi")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe de Développement")
                                .email("contact@recrutement-app.com")
                                .url("https://recrutement-app.com"))
                        .license(new License()
                                .name("Licence Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")));
    }
}
