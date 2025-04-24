package com.sharedsystemshome.dsa.security.service;

import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.model.Token;
import com.sharedsystemshome.dsa.security.repository.TokenRepository;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.util.CustomValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private TokenRepository tokenMockRepo;

    @Mock
    private UserAccountRepository userMockRepo;

    @Mock
    private CustomValidator<Token> validator;

    @Mock
    private UserContextService userContextService;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.tokenService = new TokenService(
                this.tokenMockRepo,
                this.userMockRepo,
                this.userContextService,
                this.validator);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateToken() {
        Long id = 1L;
        Token token = Token.builder()
                .build();

        Token savedToken = Token.builder()
                .id(id)
                .build();

        when(this.tokenMockRepo.save(token)).thenReturn(savedToken);

        Long result = this.tokenService.createToken(token);

        assertNotNull(result);
        assertEquals(id, result);
        verify(this.tokenMockRepo, times(1)).save(token);
    }

    @Test
    public void testCreateToken_TokenIsNull() {

        Token token = null;

        Exception e = assertThrows(SecurityValidationException.class,
                () -> this.tokenService.createToken(token));

        assertEquals("Token is null or empty.", e.getMessage());

        verify(this.tokenMockRepo, times(0)).save(token);

    }

    @Test
    public void testCreateToken_SaveThrowsException() {

       Token token = Token.builder()
                .build();

        when(this.tokenMockRepo.save(token)).thenThrow(IllegalArgumentException.class);

        Exception e = assertThrows(SecurityValidationException.class,
                () -> this.tokenService.createToken(token));

        assertEquals("Unable to add or update Token.", e.getMessage());

        verify(this.tokenMockRepo, times(1)).save(token);

    }



    @Test
    void testGetTokens() {

        List<Token> tokens = new ArrayList<>();
        when(this.tokenMockRepo.findAll()).thenReturn(tokens);

        List<Token> result = this.tokenService.getTokens();

        assertNotNull(result);
        assertEquals(tokens, result);
        verify(this.tokenMockRepo, times(1)).findAll();
    }

    @Test
    void testRevokeUserTokens() {

        //Given
        Long id = 1L;
        String email = "user@email.com";
        UserAccount user = UserAccount.builder()
                .id(id)
                .email(email)
                .build();

        when(this.userMockRepo.existsByEmail(anyString())).thenReturn(true);

        List<Token> tokens = List.of(new Token(), new Token(), new Token());

        when(this.tokenMockRepo.findAllValidTokensByUser(id)).thenReturn(tokens);

        //Then
        tokens.forEach(token -> {
            assertEquals(false, token.getRevoked());
            assertEquals(false, token.getExpired());
        });

        //When
        this.tokenService.revokeUserTokens(user);

        //Then
        tokens.forEach(token -> {
            assertEquals(true, token.getRevoked());
            assertEquals(true, token.getExpired());
        });

        verify(this.tokenMockRepo, times(1)).saveAll(tokens);
    }

    @Test
    void testRevokeUserTokens_InvalidEmail() {

        //Given
        Long id = 1L;
        String email = "user@email.com";
        UserAccount user = UserAccount.builder()
                .id(id)
                .email(email)
                .build();

        when(this.userMockRepo.existsByEmail(anyString())).thenReturn(false);

        Exception e = assertThrows(SecurityValidationException.class,
                () -> this.tokenService.revokeUserTokens(user));

        assertEquals("User Account with email = " + email + " not found.", e.getMessage());

        verify(this.tokenMockRepo, times(0)).saveAll(any());
    }

    @Test
    void testRevokeUserTokens_InvalidUser() {

        //Given
        Exception e = assertThrows(SecurityValidationException.class,
                () -> this.tokenService.revokeUserTokens(null));

        assertEquals("User Account is null or empty.", e.getMessage());

        verify(this.tokenMockRepo, times(0)).saveAll(any());
    }

}