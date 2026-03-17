package com.sharedsystemshome.dsa.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security.config.cors-global-config")
public record CorsGlobalConfigProperties(
        String addMapping,
        List<String> allowedMethods,
        List<String> allowedHeaders,
        List<String> allowedOrigins,
        Long maxAge,
        Boolean allowCredentials
) {
}
