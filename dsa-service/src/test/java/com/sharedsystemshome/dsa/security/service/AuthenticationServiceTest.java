package com.sharedsystemshome.dsa.security.service;

import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.security.dto.UserAuthentication;
import com.sharedsystemshome.dsa.security.dto.UserLogin;
import com.sharedsystemshome.dsa.security.dto.UserRegistration;
import com.sharedsystemshome.dsa.security.model.*;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import com.sharedsystemshome.dsa.security.repository.TokenRepository;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.util.CustomValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserAccountRepository userMockRepo;

    @Mock
    private TokenRepository tokenMockRepo;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private RoleRepository roleMockRepo;

    @Mock
    private CustomValidator<Token> tokenValidator;

    @Mock
    private CustomValidator<UserAccount> userValidator;

    @Mock
    private UserContextService userContextService;

    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        TokenService tokenService = new TokenService(
                this.tokenMockRepo,
                this.userMockRepo,
                this.userContextService,
                this.tokenValidator
               );

        this.authenticationService =
                new AuthenticationService(
                        this.userMockRepo,
                        this.passwordEncoder,
                        this.roleMockRepo,
                        this.jwtService,
                        tokenService,
                        this.authManager,
                        this.userContextService,
                        this.userValidator
                );
    }


    @AfterEach
    void tearDown() {
    }

    @Test
    void testRegister() {
        //Given
        String firstName = "John";
        String lastName = "Doe";
        String contact = "99999999";
        String email = "john@email.com";
        String rawPassword = "12345";
        Role role = new Role();
        role.setId(3L);
        UserRegistration reg = UserRegistration.builder()
                .firstName(firstName)
                .lastName(lastName)
                .contactNumber(contact)
                .email(email)
                .password(rawPassword)
                .roles(List.of(role))
                .build();

        when(this.passwordEncoder.encode(rawPassword)).thenReturn(
                "dummyencryptedpasswordstring");

        Long id = 1L;
        UserAccount savedUser = UserAccount.builder()
                .id(id)
                .firstName(reg.getFirstName())
                .lastName(reg.getLastName())
                .contactNumber(reg.getContactNumber())
                .email(reg.getEmail())
                .password(reg.getPassword())
                .roles(List.of(role))
                .build();

        when(this.userMockRepo.save(any())).thenReturn(savedUser);

        when(this.roleMockRepo.findAll()).thenReturn(List.of(role));

        String tokenString = "dummytokenstring";
        Token token = Token.builder()
                .token(tokenString)
                .user(savedUser)
                .build();

        when(this.jwtService.generateAccessToken(any())).thenReturn(token);
        when(this.jwtService.generateRefreshToken(any())).thenReturn(token);

        Token savedToken = Token.builder()
                .id(id)
                .token(token.getToken())
                .user(token.getUser())
                .build();

        when(this.tokenMockRepo.save(token)).thenReturn(savedToken);

        when(this.userMockRepo.findById(savedUser.getId())).thenReturn(Optional.of(savedUser));

        UserAuthentication result = this.authenticationService.register(reg);

        assertNotNull(result);
        assertEquals(tokenString, result.getAccessToken());
        verify(this.passwordEncoder, times(1)).encode(rawPassword);
        verify(this.userMockRepo, times(1)).save(any());
        verify(this.jwtService, times(1)).generateAccessToken(any());
        verify(this.tokenMockRepo, times(2)).save(any());

    }

    @Test
    void testRegister_NullUserRegistration() {

        UserRegistration reg = null;

        Exception e = assertThrows(SecurityValidationException.class,
                () -> this.authenticationService.register(reg));

        assertEquals("User Account is null or empty.", e.getMessage());

        verify(this.userMockRepo, times(0)).save(any());

    }

    @Test
    void testRegister_InvalidUser() {
        Role role = new Role();
        role.setId(1L);

        UserRegistration reg = UserRegistration.builder()
                .firstName("John")
                .lastName("Doe")
                .email("invalid@email.com")
                .password("password")
                .roles(List.of(role))
                .build();

        when(this.userMockRepo.existsByEmail(anyString())).thenReturn(false);
        when(this.roleMockRepo.findAll()).thenReturn(List.of(role));
        when(this.userMockRepo.save(any())).thenThrow(new RuntimeException());

        Exception e = assertThrows(SecurityValidationException.class,
                () -> this.authenticationService.register(reg));

        assertTrue(e.getMessage().contains("Unable to add or update User Account."));
    }

    @Test
    void testRegister_InvalidToken() {
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@email.com";
        String rawPassword = "dummyrawpassword";
        Role role = new Role();
        role.setId(1L);

        UserRegistration reg = UserRegistration.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(rawPassword)
                .email(email)
                .roles(List.of(role))
                .build();

        when(this.userMockRepo.existsByEmail(email)).thenReturn(false);
        when(this.passwordEncoder.encode(rawPassword)).thenReturn("encrypted");

        Long id = 17L;
        UserAccount user = UserAccount.builder()
                .id(id)
                .email(email)
                .roles(List.of(role))
                .build();

        when(this.userMockRepo.save(any())).thenReturn(user);
        when(this.userMockRepo.findById(id)).thenReturn(Optional.of(user));
        when(this.roleMockRepo.findAll()).thenReturn(List.of(role));

        // Simulate null token
        when(this.jwtService.generateAccessToken(any())).thenReturn(null);

        Exception e = assertThrows(SecurityValidationException.class,
                () -> this.authenticationService.register(reg));

        assertEquals("Unable to add or update Token.", e.getMessage());
        verify(this.userMockRepo, times(1)).save(any());
        verify(this.tokenMockRepo, never()).save(any());
    }

    @Test
    void testAuthenticate() {
        String email = "john@email.com";
        String tokenString = "dummytokenstring";

        UserAccount user = UserAccount.builder()
                .id(1L)
                .email(email)
                .build();

        UserLogin login = UserLogin.builder()
                .email(email)
                .password("password")
                .build();

        when(this.authManager.authenticate(any())).thenReturn(null);
        when(this.userMockRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(this.userMockRepo.existsByEmail(email)).thenReturn(true);

        Token jwtToken = Token.builder().token(tokenString).user(user).build();
        Token refreshToken = Token.builder().token("refreshtoken").user(user).build();

        when(this.jwtService.generateAccessToken(user)).thenReturn(jwtToken);
        when(this.jwtService.generateRefreshToken(user)).thenReturn(refreshToken);
        when(this.tokenMockRepo.save(any())).thenReturn(jwtToken); // simplify for test

        UserAuthentication result = this.authenticationService.authenticate(login);

        assertNotNull(result);
        assertEquals(tokenString, result.getAccessToken());
        assertEquals("refreshtoken", result.getRefreshToken());
    }


    @Test
    void testAuthenticate_NullUserLogin() {

        UserLogin login = null;

        Exception e = assertThrows(SecurityValidationException.class,
                () -> this.authenticationService.authenticate(login));

        assertEquals("User Login is null or empty.", e.getMessage());

        verify(this.userMockRepo, times(0)).save(any());

    }

    @Test
    void tesAuthentication_ThrowsException() {

        String email = "user@email.com";
        UserLogin login = UserLogin.builder()
                .email(email)
                .build();

        String authExceptionMsg = "Something went wrong!";
        when(this.authManager.authenticate(any())).thenThrow(new RuntimeException(authExceptionMsg));

        Exception e = assertThrows(SecurityValidationException.class,
                () -> this.authenticationService.authenticate(login));

        assertEquals("Invalid username or password.", e.getMessage());
        verify(this.authManager, times(1)).authenticate(any());

    }

    @Test
    void testAuthentication_InvalidUser() {
        String email = "user@email.com";
        UserLogin login = UserLogin.builder()
                .email(email)
                .password("test")
                .build();

        when(this.authManager.authenticate(any())).thenReturn(null);
        when(this.userMockRepo.findByEmail(email)).thenReturn(Optional.empty());

        Exception e = assertThrows(SecurityValidationException.class,
                () -> this.authenticationService.authenticate(login));

        assertEquals("Invalid username.", e.getMessage());
    }

    @Test
    void testRefreshToken() throws Exception {
        String jwt = "validtoken";
        String email = "test@email.com";
        Long custId = 100L;

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class); // ✅ required mock

        CustomerAccount cust = CustomerAccount.builder()
                .id(custId)
                .build();

        UserAccount user = UserAccount.builder()
                .id(1L)
                .email(email)
                .parentAccount(cust)
                .build();

        // ✅ Stubbing for refreshToken flow
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + jwt);
        when(jwtService.extractJwtUserName(jwt)).thenReturn(email);
        when(jwtService.extractCustomerAccountId(jwt)).thenReturn(custId);
        when(userMockRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(jwt, user)).thenReturn(true);
        when(response.getWriter()).thenReturn(writer); // ✅ fix applied

        when(jwtService.generateAccessToken(user)).thenReturn(Token.builder().token("newtoken").user(user).build());
        when(jwtService.generateRefreshToken(user)).thenReturn(Token.builder().token("newrefreshtoken").user(user).build());
        when(userMockRepo.existsByEmail(email)).thenReturn(true);
        when(tokenMockRepo.save(any())).thenReturn(mock(Token.class));

        authenticationService.refreshToken(request, response);

        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).flush(); // ✅ ensure flushing occurred
    }


    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        String jwt = "invalidtoken";
        String email = "test@email.com";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + jwt);
        when(jwtService.extractJwtUserName(jwt)).thenReturn(email);
        when(userMockRepo.findByEmail(email)).thenReturn(Optional.of(UserAccount.builder().email(email).build()));
        when(jwtService.isTokenValid(eq(jwt), any())).thenReturn(false);

        when(response.getWriter()).thenReturn(writer);

        authenticationService.refreshToken(request, response);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("{\"error\":\"Invalid or expired refresh token.\"}");
    }

    @Test
    void testLogout() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);

        String token = "valid.token.here";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Token mockToken = new Token();
        mockToken.setToken(token);
        when(tokenMockRepo.findByToken(token)).thenReturn(Optional.of(mockToken));

        this.authenticationService.logout(request, response, authentication);

        assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
        verify(tokenMockRepo, times(1)).findByToken(token);
    }

    @Test
    void testLogout_WithMissingAuthorizationHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);

        // Don't stub header to avoid unnecessary stubbing warning

        this.authenticationService.logout(request, response, authentication);

        assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testLogout_WithValidToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);

        String validToken = "valid.token.here";
        Token mockToken = new Token();
        mockToken.setToken(validToken);
        mockToken.setExpired(false);
        mockToken.setRevoked(false);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(tokenMockRepo.findByToken(validToken)).thenReturn(Optional.of(mockToken));

        this.authenticationService.logout(request, response, authentication);

        assertTrue(mockToken.getExpired());
        assertTrue(mockToken.getRevoked());
        assertNotNull(mockToken.getRevokedAt());
        verify(tokenMockRepo).findByToken(validToken);
        verify(tokenMockRepo).save(mockToken);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}
