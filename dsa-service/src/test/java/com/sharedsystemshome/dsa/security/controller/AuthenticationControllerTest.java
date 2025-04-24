package com.sharedsystemshome.dsa.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.dto.UserAuthentication;
import com.sharedsystemshome.dsa.security.dto.UserLogin;
import com.sharedsystemshome.dsa.security.dto.UserRegistration;
import com.sharedsystemshome.dsa.security.model.Token;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import com.sharedsystemshome.dsa.security.service.AuthenticationService;
import com.sharedsystemshome.dsa.security.service.JwtService;
import com.sharedsystemshome.dsa.security.service.TokenService;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.service.UserAccountService;
import com.sharedsystemshome.dsa.util.CustomValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Optional;

public class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authController;

    @Mock
    private AuthenticationService authMockService;

    @Mock
    private TokenService tokenMockService;

    @Mock
    private JwtService jwtMockService;

    @Mock
    private UserAccountRepository userMockRepo;


    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.authController).build();
/*
        this.authMockService = new AuthenticationService(
                null,
               null,
                null,
               null,
                this.tokenMockService,
                null,
               null,
                null);*/



    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testRegister() throws Exception {

        UserRegistration reg = UserRegistration.builder()
                .build();

        String tokenString = "dummyTokenString";
        UserAuthentication userAuth =  UserAuthentication.builder()
                .accessToken(tokenString)
                .build();

        when(this.authMockService.register(reg)).thenReturn(userAuth);

        String req = new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(reg);

        String res = new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(userAuth)
                .replaceAll("\\s","");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .content(req)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(res))
                .andReturn();

        verify(this.authMockService, times(1))
                .register(reg);
    }

    @Test
    void testAuthenticate() throws Exception {

        UserLogin login = UserLogin.builder()
                .build();

        String tokenString = "dummyTokenString";
        UserAuthentication userAuth =  UserAuthentication.builder()
                .accessToken(tokenString)
                .build();

        when(this.authMockService.authenticate(login)).thenReturn(userAuth);

        String req = new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(login);

        String res = new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(userAuth)
                .replaceAll("\\s","");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/authenticate")
                        .content(req)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(res))
                .andReturn();

        verify(this.authMockService, times(1))
                .authenticate(login);
    }

    @Test
    void testRefreshToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer dummyRefreshToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        doNothing().when(this.authMockService)
                .refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));

        authController.refreshToken(request, response);

        verify(this.authMockService, times(1))
                .refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void testLogout() {

        ResponseEntity<?> result = this.authController.logout(request, response);

        verify(this.authMockService, times(1)).logout(request, response, null);

        assertEquals(200, result.getStatusCode().value());
    }
}