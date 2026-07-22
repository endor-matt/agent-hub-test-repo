package com.skybook.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI skyBookOpenApi() {
        final String scheme = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("SkyBook AI Backend API")
                        .description("""
                                Flight booking REST API for the SkyBook AI security research & training lab.
                                This API is NOT production-secure. Training-mode vulnerable endpoints are disabled by default.
                                """)
                        .version("1.0.0-TRAINING")
                        .contact(new Contact().name("SkyBook Lab").email("lab@skybook.local"))
                        .license(new License().name("Training Use Only")))
                .addSecurityItem(new SecurityRequirement().addList(scheme))
                .components(new Components().addSecuritySchemes(scheme,
                        new SecurityScheme()
                                .name(scheme)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
