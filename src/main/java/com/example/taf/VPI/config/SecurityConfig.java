package com.example.taf.VPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 Security configuration handling:
 API endpoint authorization
 CORS setup for security layer
 CSRF protection configuration
 */
@Configuration
public class SecurityConfig {

    /**
     Configures security filter chain with:
     Custom CORS configuration
     CSRF disabled for API endpoints
     Authorization rules for endpoints
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Apply CORS configuration from custom source
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable CSRF protection for stateless API
                .csrf(AbstractHttpConfigurer::disable)
                // Configure endpoint access rules
                .authorizeHttpRequests(authorize -> authorize
                        // Public authentication endpoints
                        .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // All other API endpoints require no authentication
                        .requestMatchers("/api/**").permitAll()
                        // Secure all other requests
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     Creates CORS configuration aligned with frontend requirements
     @return CORS configuration source with allowed origins and methods
    */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow frontend origin
        configuration.setAllowedOrigins(List.of("https://venomous-plant-identifier-frontend.vercel.app",
                "http://localhost:3000"));
        // Supported HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT"));
        // All headers permitted
        configuration.setAllowedHeaders(List.of("*"));
        // Allow cookies/credentials
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply configuration to all endpoints
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
