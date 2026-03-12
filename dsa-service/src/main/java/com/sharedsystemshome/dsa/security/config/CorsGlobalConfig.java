package com.sharedsystemshome.dsa.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsGlobalConfig {

    @Bean
    public WebMvcConfigurer corsGlobalConfigurer(CorsGlobalConfigProperties corsConfig) {

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping(corsConfig.addMapping())
                        .allowedMethods(corsConfig.allowedMethods().toArray(new String[0]))
                        .allowedHeaders(corsConfig.allowedHeaders().toArray(new String[0]))
                        .allowedOrigins(corsConfig.allowedOrigins().toArray(new String[0]))
                        .maxAge(corsConfig.maxAge())
                        .allowCredentials(corsConfig.allowCredentials());
            }
        };
    }
}