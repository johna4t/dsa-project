package com.sharedsystemshome.dsa.security.filters;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OriginVerificationProperties.class)
public class OriginVerificationConfig {

    @Bean
    public OriginVerificationFilter originVerificationFilter(
            OriginVerificationProperties originVerificationProperties) {
        return new OriginVerificationFilter(originVerificationProperties);
    }
}