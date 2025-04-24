
package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.enums.UserAccountStatus;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.security.enums.PermissionType;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.model.Permission;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.repository.PermissionRepository;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserAccountRepositoryTest {

    @Autowired
    private UserAccountRepository testSubject;

    @Autowired
    private CustomerAccountRepository customerRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PermissionRepository permissionRepo;

    private CustomerAccount cust;
    private Role role;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setup() {
        Permission permission = Permission.builder()
                .name(PermissionType.USER_READ)
                .build();
        permissionRepo.save(permission);

        role = Role.builder()
                .name(RoleType.USER)
                .permissions(List.of(permission))
                .build();
        roleRepo.save(role);

        cust = CustomerAccount.builder()
                .name("TestCust")
                .departmentName("Dept")
                .url("http://test.com")
                .branchName("HQ")
                .build();
        customerRepo.save(cust);
    }

    @Test
    void testSave() {
        String email = "ts@email.com";
        UserAccount user = UserAccount.builder()
                .parentAccount(cust)
                .firstName("Tony")
                .lastName("Stark")
                .contactNumber("99999999")
                .email(email)
                .jobTitle("Engineer")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .roles(List.of(role))
                .build();

        Long userId = testSubject.save(user).getId();

        assertEquals(1, testSubject.count());
        assertTrue(testSubject.existsById(userId));
        UserAccount found = testSubject.findById(userId).get();

        assertEquals(email, found.getEmail());
        assertEquals(UserAccountStatus.ACTIVE, found.getStatus());
        assertEquals(true, found.isAccountNonExpired());
    }

    @Test
    void testSave_InvalidUser1() {
        UserAccount user = UserAccount.builder()
                .firstName("Tony")
                .contactNumber("99999999")
                .email("ts@email.com")
                .password("12345")
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            testSubject.save(user);
            testSubject.flush();
        });
    }

    @Test
    void testExistsByEmail_UserExists() {
        String email = "exist@test.com";
        UserAccount user = buildBasicUser(email);
        testSubject.save(user);

        assertTrue(testSubject.existsByEmail(email));
    }

    @Test
    void testExistsByEmail_UserDoesNotExist() {
        UserAccount user = buildBasicUser("doesnotexist@test.com");
        testSubject.save(user);

        assertFalse(testSubject.existsByEmail("nonexistent@test.com"));
    }

    @Test
    void testFindByEmail_UserExists() {
        String email = "bruce@test.com";
        UserAccount user = buildBasicUser(email);
        Long userId = testSubject.save(user).getId();

        UserAccount found = testSubject.findByEmail(email).orElseThrow();

        assertEquals(userId, found.getId());
        assertEquals(email, found.getEmail());
    }

    @Test
    void testFindByEmail_UserDoesNotExist() {
        assertThrows(NoSuchElementException.class, () -> {
            testSubject.findByEmail("missing@test.com").orElseThrow();
        });
    }

    @Test
    void testFindUserAccountByParentAccountId() {
        UserAccount user = buildBasicUser("find@parent.com");
        testSubject.save(user);

        List<UserAccount> users = testSubject.findUserAccountByParentAccountId(cust.getId()).orElseThrow();
        assertFalse(users.isEmpty());
        assertEquals("find@parent.com", users.get(0).getEmail());
    }

    @Test
    void testFindByIdAndParentAccountId() {
        UserAccount user = buildBasicUser("lookup@combo.com");
        Long userId = testSubject.save(user).getId();

        UserAccount found = testSubject.findByIdAndParentAccountId(userId, cust.getId()).orElseThrow();
        assertEquals("lookup@combo.com", found.getEmail());
    }

    @Test
    void testCountByRoleNameAndParentAccountId() {
        UserAccount user = buildBasicUser("count@role.com");
        testSubject.save(user);

        Long count = testSubject.countByRoleNameAndParentAccountId(RoleType.USER, cust.getId());
        assertEquals(1L, count);
    }

    void testDeleteUserAccountById() {
        UserAccount user = buildBasicUser("delete@me.com");
        Long userId = testSubject.save(user).getId();

        testSubject.deleteUserAccountById(userId);

        // Flush and clear to ensure persistence context is updated
        entityManager.flush();
        entityManager.clear();

        assertFalse(testSubject.findById(userId).isPresent());
    }

    private UserAccount buildBasicUser(String email) {
        return UserAccount.builder()
                .parentAccount(cust)
                .firstName("Test")
                .lastName("User")
                .contactNumber("0000000")
                .email(email)
                .jobTitle("Analyst")
                .password("encodedPwd")
                .roles(List.of(role))
                .build();
    }
}
