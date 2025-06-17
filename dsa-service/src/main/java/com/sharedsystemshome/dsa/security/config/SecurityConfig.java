package com.sharedsystemshome.dsa.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import static com.sharedsystemshome.dsa.security.enums.PermissionType.*;
import static com.sharedsystemshome.dsa.security.enums.RoleType.*;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/authenticate",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh-token",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/error"
    };

    private static final String[] MEMBER_SECURED_ENDPOINTS = {
            "/api/v1/data-sharing-parties/**",
            "/api/v1/data-content-definitions/**",
            "/api/v1/data-flows/**",
            "/api/v1/data-processors/**",
            "/api/v1/data-sharing-agreements/**",
            "/api/v1/personal-profiles/*",
            "/api/v1/auth/logout"
    };

    private static final String[] ACCOUNT_ADMIN_SECURED_ENDPOINTS = {
            "/api/v1/user-accounts/**"
    };

    private static final String[] SUPER_ADMIN_SECURED_ENDPOINTS = {
            "/api/v1/tokens/**",
            "/api/v1/roles/**",
            "/api/v1/permissions/**"
    };

    private static final String[] CUSTOMER_ACCOUNT = {
            "/api/v1/customer-accounts/**"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSec) throws Exception {

        httpSec
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req
                                // Unsecured endpoints
                                .requestMatchers(WHITE_LIST_URL).permitAll()
                                .requestMatchers(HttpMethod.POST, CUSTOMER_ACCOUNT).permitAll()

                                //MEMBER authorisations for GET, POST, PUT and DELETE
                                .requestMatchers(MEMBER_SECURED_ENDPOINTS).authenticated()
                                .requestMatchers(HttpMethod.GET, CUSTOMER_ACCOUNT).hasAnyAuthority(
                                        MEMBER_READ.name(), ACCOUNT_ADMIN_READ.name(), SUPER_ADMIN_READ.name())

                                 //ACCOUNT_ADMIN authorisations
                                .requestMatchers(ACCOUNT_ADMIN_SECURED_ENDPOINTS).hasAnyRole(ACCOUNT_ADMIN.name(), SUPER_ADMIN.name())
                                .requestMatchers(HttpMethod.PUT, CUSTOMER_ACCOUNT).hasAnyAuthority(ACCOUNT_ADMIN_UPDATE.name(), SUPER_ADMIN_UPDATE.name())

                                //SUPER_ADMIN only authorisations
                                .requestMatchers(SUPER_ADMIN_SECURED_ENDPOINTS).hasAnyRole(SUPER_ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, CUSTOMER_ACCOUNT).hasAnyRole(SUPER_ADMIN.name())

                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(this.authenticationProvider)
                .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSec.build();

    }

}
