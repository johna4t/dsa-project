package com.sharedsystemshome.dsa.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsGlobalConfig {

    private static final String OPTIONS = "OPTIONS";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String PATCH = "PATCH";
    private static final String DELETE = "DELETE";

    @Bean
    public WebMvcConfigurer corsGlobalConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods(OPTIONS, GET, POST, PUT, PATCH, DELETE)
                        .allowedHeaders("*")
//                        .allowedOriginPatterns("*")
                        .allowedOrigins("http://localhost:4200")
                        .maxAge(3600)
                        .allowCredentials(true);
            }
        };
    }
}