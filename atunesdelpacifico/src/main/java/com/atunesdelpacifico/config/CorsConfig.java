package com.atunesdelpacifico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir orígenes específicos
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173"
        ));

        // Permitir métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Permitir headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Exponer headers
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}