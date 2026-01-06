package com.iae.truco_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Usamos patterns que es más robusto para validación con credentials
                .allowedOriginPatterns(
                    "http://localhost:5173",
                    "http://trucoapp.mardelplatadev.cloud",
                    "https://trucoapp.mardelplatadev.cloud"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}