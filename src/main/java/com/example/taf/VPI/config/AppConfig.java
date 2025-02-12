package com.example.taf.VPI.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {
    /**
     Configures RestTemplate for HTTP client operations
     @param builder Autoconfigured RestTemplate builder
     @return RestTemplate instance for making HTTP requests
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    //Allows frontend hosted at Vercel to access API endpoints
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // Frontend deployment URL
                        .allowedOrigins("https://venomous-plant-identifier-frontend.vercel.app/")
                        // Supported HTTP methods
                        .allowedMethods("GET", "POST", "DELETE", "PUT")
                        // All headers allowed
                        .allowedHeaders("*")
                        // Allow cookies/authentication credentials
                        .allowCredentials(true);
            }
        };
    }
}
