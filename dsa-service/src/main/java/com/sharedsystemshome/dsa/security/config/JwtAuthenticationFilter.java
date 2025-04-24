package com.sharedsystemshome.dsa.security.config;

import com.sharedsystemshome.dsa.security.repository.TokenRepository;
import com.sharedsystemshome.dsa.security.service.JwtService;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.model.UserAccount;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserContextService userContextService; // ✅ Inject UserContextService

    static final String AUTH_HEADER_NAME = "Authorization";
    static final String AUTH_HEADER_START = "Bearer ";
    static final Integer AUTH_HEADER_START_LEN = AUTH_HEADER_START.length();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTH_HEADER_NAME);
        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_START)) {
            // Allow to proceed to verify user
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(AUTH_HEADER_START_LEN);
        final String jwtUserName = jwtService.extractJwtUserName(jwt);
        final Long customerAccountId = jwtService.extractCustomerAccountId(jwt); // ✅ Extract CustomerAccount ID

        if (jwtUserName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserAccount user = (UserAccount) userDetailsService.loadUserByUsername(jwtUserName);

            if (jwtService.isTokenValid(jwt, user)) {
                // ✅ Store user details in SecurityContext using UserContextService
                userContextService.setAuthenticatedUser(user, customerAccountId);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
