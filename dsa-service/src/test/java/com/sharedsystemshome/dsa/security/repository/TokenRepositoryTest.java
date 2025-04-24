package com.sharedsystemshome.dsa.security.repository;

import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.repository.CustomerAccountRepository;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.enums.TokenType;
import com.sharedsystemshome.dsa.security.model.Permission;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.model.Token;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TokenRepositoryTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Autowired
    private TokenRepository testSubject;

    @Autowired
    private UserAccountRepository userRepo;

    @Autowired
    private CustomerAccountRepository customerRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PermissionRepository permissionRepo;

    @Test
    void testSave(){
        //Given
        String firstName = "Tony";
        String lastName = "Stark";
        String contact = "99999999";
        String email = "ts@email.com";
        String encodedPassword = new BCryptPasswordEncoder().encode("12345");
        Permission permission = this.permissionRepo.save(new Permission());
        Role role = Role.builder()
                .permissions(List.of(permission))
                .build();
        this.roleRepo.save(role);

        CustomerAccount cust = CustomerAccount.builder()
                .name("Cust")
                .departmentName("Cust dept")
                .url("www.cust.com")
                .branchName("Test BU")
                .build();

        this.customerRepo.save(cust);

        UserAccount user = UserAccount.builder()
                .parentAccount(cust)
                .firstName(firstName)
                .lastName(lastName)
                .contactNumber(contact)
                .email(email)
                .password(encodedPassword)
                .roles(List.of(role))
                .build();

        Long userId = this.userRepo.save(user).getId();

        String tokenString = "dummytokenstring";
        Token token = Token.builder()
                .token(tokenString)
                .user(user)
                .build();

        //When
        Long tokenId = this.testSubject.save(token).getId();

        // Then

        // Assertion: repository should contain one user.
        assertEquals(1, this.testSubject.count());
        // Assertion: user should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(tokenId));
        // Assertion: saved user should return supplied details.
        assertEquals(tokenString, this.testSubject.findById(tokenId).get().getToken());
        assertEquals(userId, this.testSubject.findById(tokenId).get().getUser().getId());

        //Verify defaults
        assertEquals(TokenType.ACCESS, this.testSubject.findById(tokenId).get().getTokenType());
        assertEquals(false, this.testSubject.findById(tokenId).get().getExpired());
        assertEquals(false, this.testSubject.findById(tokenId).get().getRevoked());

    }

    @Test
    void testSave_NullTokenString() {

        //Given
        String firstName = "Tony";
        String lastName = "Stark";
        String contact = "99999999";
        String email = "ts@email.com";
        String encodedPassword = new BCryptPasswordEncoder().encode("12345");

        CustomerAccount cust = CustomerAccount.builder()
                .name("Cust")
                .departmentName("Cust dept")
                .url("www.cust.com")
                .branchName("Test BU")
                .build();

        this.customerRepo.save(cust);

        UserAccount user = UserAccount.builder()
                .parentAccount(cust)
                .firstName(firstName)
                .lastName(lastName)
                .contactNumber(contact)
                .email(email)
                .password(encodedPassword)
                .build();

        this.userRepo.save(user);

        Token token = Token.builder()
                .build();

        //Then
        Exception e = assertThrows(DataIntegrityViolationException.class, () -> {
            //When
            this.testSubject.save(token);
            this.testSubject.flush();
        });

    }

    @Test
    void testSave_InvalidUser() {

        //Given
        String tokenString = "dummytokenstring";
        Token token = Token.builder()
                .token(tokenString)
                .build();

        //Then
        Exception e = assertThrows(DataIntegrityViolationException.class, () -> {
            //When
            this.testSubject.save(token);
            this.testSubject.flush();
        });

    }

    @Test
    void testFindAllValidTokensByUser() {

        //Given
        String firstName = "Tony";
        String lastName = "Stark";
        String contact = "99999999";
        String email = "ts@email.com";
        String encodedPassword = new BCryptPasswordEncoder().encode("12345");
        Permission permission = this.permissionRepo.save(new Permission());
        Role role = Role.builder()
                .permissions(List.of(permission))
                .build();
        this.roleRepo.save(role);

        CustomerAccount cust = CustomerAccount.builder()
                .name("Cust")
                .departmentName("Cust dept")
                .url("www.cust.com")
                .branchName("Test BU")
                .build();

        this.customerRepo.save(cust);

        UserAccount user = UserAccount.builder()
                .parentAccount(cust)
                .firstName(firstName)
                .lastName(lastName)
                .contactNumber(contact)
                .email(email)
                .password(encodedPassword)
                .roles(List.of(role))
                .build();

        Long userId = this.userRepo.save(user).getId();

        String tokenString1 = "dummytokenstring1";
        Token token1 = Token.builder()
                .token(tokenString1)
                .user(user)
                .expired(true)
                .revoked(true)
                .build();

        String tokenString2 = "dummytokenstring2";
        Token token2 = Token.builder()
                .token(tokenString2)
                .user(user)
                .build();

        String tokenString3 = "dummytokenstring3";
        Token token3 = Token.builder()
                .token(tokenString3)
                .user(user)
                .expired(true)
                .revoked(true)
                .build();

        this.testSubject.saveAll(List.of(token1, token2, token3));

        //When
        List<Token> validTokens = this.testSubject.findAllValidTokensByUser(userId);

        //Then
        assertEquals(1, validTokens.size());
        assertEquals(tokenString2, validTokens.get(0).getToken());

    }

}