package com.atunesdelpacifico.config;

import com.atunesdelpacifico.security.JwtAuthenticationEntryPoint;
import com.atunesdelpacifico.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos de autenticación
                        .requestMatchers("/auth/**").permitAll()

                        // Endpoints públicos de productos (solo GET)
                        .requestMatchers(HttpMethod.GET, "/productos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()

                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Endpoints de productos que requieren autenticación (POST, PUT, DELETE)
                        .requestMatchers(HttpMethod.POST, "/productos/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/productos/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/productos/**").authenticated()

                        // Endpoints por roles (mantener tu configuración existente)
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/operador/**").hasAnyRole("ADMINISTRADOR", "OPERADOR")
                        .requestMatchers("/cliente/**").hasAnyRole("ADMINISTRADOR", "OPERADOR", "CLIENTE")

                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated()
                );

        // Agregar filtro JWT
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
