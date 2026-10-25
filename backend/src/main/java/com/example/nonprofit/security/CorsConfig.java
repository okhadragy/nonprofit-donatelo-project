package com.example.nonprofit.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Lets the React dev server (Vite, port 5173) call this API from the browser.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:5173",
                    "http://127.0.0.1:5173"
                )
                .allowedMethods(
                    "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
                )
                .allowedHeaders("Authorization", "Content-Type");
    }
}
