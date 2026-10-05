package com.cinemalog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cinemaLogOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cinema Log & Release Radar API")
                        .version("v1")
                        .description("REST API for the movie diary. Log in at /login first (demo@cinemalog.app / cinema123); "
                                + "Swagger then reuses the browser session cookie."))
                .components(new Components().addSecuritySchemes("session",
                        new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.COOKIE).name("JSESSIONID")))
                .addSecurityItem(new SecurityRequirement().addList("session"));
    }
}
