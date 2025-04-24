package com.sharedsystemshome.dsa.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.dto.UserAuthentication;
import com.sharedsystemshome.dsa.security.dto.UserLogin;
import com.sharedsystemshome.dsa.security.dto.UserRegistration;
import com.sharedsystemshome.dsa.security.model.*;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.service.UserAccountService;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.util.CustomValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.sharedsystemshome.dsa.util.BusinessValidationException.*;

@Service
@Validated
public class AuthenticationService extends UserAccountService implements LogoutHandler {

    private final JwtService jwtService;
    private final TokenService tokenService;
    private final AuthenticationManager authManager;
    private final CustomValidator<UserLogin> validator;

    private static final Logger logger = LoggerFactory.getLogger(UserAccountService.class);
    private final UserContextService userContextService;

    static final String AUTH_HEADER_START = "Bearer ";
    static final Integer AUTH_HEADER_START_LEN = AUTH_HEADER_START.length();

    @Autowired
    public AuthenticationService(
            UserAccountRepository userRepo,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            JwtService jwtService,
            TokenService tokenService,
            AuthenticationManager authManager,
            UserContextService userContextService,
            CustomValidator validator
    ) {
        super(userRepo, passwordEncoder, roleRepository, validator);
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.authManager = authManager;
        this.validator = validator;
        this.userContextService = userContextService;
    }


    @PostMapping
    public UserAuthentication register(UserRegistration registration){

        if(null == registration){
            throw new SecurityValidationException(USER_ACCOUNT + " is null or empty.");
        }

        UserAccount user = UserAccount.builder()
                .firstName(registration.getFirstName())
                .lastName(registration.getLastName())
                .email(registration.getEmail())
                .contactNumber(registration.getContactNumber())
                .password(registration.getPassword())
                .roles(getStoredRoleValues(registration.getRoles()))
                .build();

        UserAccount userAccount = null;

        try{
            userAccount = this.getUserAccountById(this.createUserAccount(user)) ;
        } catch(BusinessValidationException e) {
            throw new SecurityValidationException("Unable to add or update " + USER_ACCOUNT + ".", e);
        }

        return this.generateResponse(userAccount);

    }

    @PostMapping
    public UserAuthentication authenticate(UserLogin login){

        if(null == login){
            throw new SecurityValidationException(USER_LOGIN + " is null or empty.");
        }

        this.validator.validate(login);

        String email = login.getEmail();

        try{
            this.authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            login.getPassword()
                    )
            );
        }catch(Exception e){
            throw new SecurityValidationException(
                    "Invalid username or password.", e);
        }

        UserAccount user = null;

        try{
            user = this.getUserAccountByEmail(email);
        } catch(BusinessValidationException e) {
            throw new SecurityValidationException("Invalid username.", e);
        }

        this.tokenService.revokeUserTokens(user);

        return this.generateResponse(user);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_START)) {
            return;
        }

        final String refreshToken = authHeader.substring(AUTH_HEADER_START_LEN);
        final String userEmail = jwtService.extractJwtUserName(refreshToken);

        if (userEmail == null) return;

        UserAccount user = this.getUserAccountByEmail(userEmail);

        if (!jwtService.isTokenValid(refreshToken, user)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"Invalid or expired refresh token.\"}");
            response.getWriter().flush();
            return;
        }

        Long tokenCustomerId = jwtService.extractCustomerAccountId(refreshToken);
        if (!user.getParentAccount().getId().equals(tokenCustomerId)) {
            throw new SecurityValidationException(TOKEN + " scope mismatch: invalid " + CUSTOMER_ACCOUNT + ".");
        }

        this.tokenService.revokeUserTokens(user);

        UserAuthentication auth = this.generateResponse(user);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), auth);
        response.getWriter().flush();
    }


    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        logger.debug("Entering CustomLogoutHandler::logout");

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String token = authHeader.substring(7);

        this.tokenService.revokeUserToken(token);

        SecurityContextHolder.clearContext();

    }

    private UserAuthentication generateResponse(UserAccount user){

        Token jwtToken = this.jwtService.generateAccessToken(user);
        Token refreshToken = this.jwtService.generateRefreshToken(user);

        try {
            this.tokenService.createToken(jwtToken);
            this.tokenService.createToken(refreshToken);
        } catch(Exception e){
            throw new SecurityValidationException(
                    "Unable to add or update " + TOKEN + ".", e);
        }

        return UserAuthentication.builder()
                .accessToken(jwtToken.getToken())
                .refreshToken(refreshToken.getToken())
                .user(user)
                .build();
    }


}
