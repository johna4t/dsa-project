package com.sharedsystemshome.dsa.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class OriginVerificationFilter extends OncePerRequestFilter {

    private static final String API_PATH_PATTERN = "/api/**";

    private final OriginVerificationProperties originVerificationProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public OriginVerificationFilter(OriginVerificationProperties originVerificationProperties) {
        this.originVerificationProperties = originVerificationProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!originVerificationProperties.enabled()) {
            return true;
        }

        String requestPath = request.getServletPath();

        if (isExemptPath(requestPath)) {
            return true;
        }

        return !pathMatcher.match(API_PATH_PATTERN, requestPath);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String configuredHeaderName = originVerificationProperties.headerName();
        String configuredHeaderValue = originVerificationProperties.headerValue();
        String actualHeaderValue = request.getHeader(configuredHeaderName);

        if (!StringUtils.hasText(configuredHeaderName) || !StringUtils.hasText(configuredHeaderValue)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Origin verification is enabled but not configured correctly");
            return;
        }

        if (!Objects.equals(configuredHeaderValue, actualHeaderValue)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExemptPath(String requestPath) {
        List<String> exemptPaths = originVerificationProperties.exemptPaths();

        if (exemptPaths == null || exemptPaths.isEmpty()) {
            return false;
        }

        return exemptPaths.stream()
                .filter(StringUtils::hasText)
                .anyMatch(exemptPath -> pathMatcher.match(exemptPath, requestPath));
    }
}