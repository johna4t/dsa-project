package com.sharedsystemshome.dsa.security.filters;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.filters.origin-verification")
public record OriginVerificationProperties(
        boolean enabled,
        String headerName,
        String headerValue,
        List<String> exemptPaths
) {
}