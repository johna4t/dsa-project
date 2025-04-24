package com.sharedsystemshome.dsa.security.repository;

import com.sharedsystemshome.dsa.security.enums.PermissionType;
import com.sharedsystemshome.dsa.security.model.Permission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public
class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository testSubject;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSaveAll(){
        // Test adding list of Permissions to repository

        // Given - create list of Permissions
        Permission p0 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_READ)
                .build();
        Permission p1 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_UPDATE)
                .build();
        Permission p2 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_CREATE)
                .build();
        Permission p3 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_DELETE)
                .build();

        List<Permission> permissions = new ArrayList<>();

        permissions.addAll(List.of(p0, p1, p2, p3));

        // When - permissions added to repository.
        this.testSubject.saveAll(permissions);

        // Then

        // Assertion: repository should contain four permissions.
        assertEquals(4, this.testSubject.count());

    }

    @Test
    void testSave_WithDuplicates(){
        // Test adding list of Permissions to repository

        // Given - create list of Permissions
        Permission p0 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_UPDATE)
                .build();
        Permission p1 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_READ)
                .build();
        Permission p2 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_UPDATE)
                .build();
        List<Permission> permissions = new ArrayList<>();

        this.testSubject.saveAll(List.of(p0, p1, p2));

        // Then - this behaviour is possibly associated with H2 database used for testng.
        // Note that saving (without flushing to db) does not in itself generate an exception.
        // An exception is generated when the repository is touched afterwards.
        // Postgres simply ignores attempts to directly insert duplicates
        Exception e = assertThrows(DataIntegrityViolationException.class, () -> {

            // When
            this.testSubject.count();
        });



    }

    @Test
    void testFindByName() throws Exception {

        // Given - create list of Permissions
        Permission p0 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_READ)
                .build();
        Permission p1 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_UPDATE)
                .build();
        Permission p2 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_CREATE)
                .build();
        Permission p3 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_DELETE)
                .build();

        List<Permission> permissions = new ArrayList<>();

        permissions.addAll(List.of(p0, p1, p2, p3));

        this.testSubject.saveAll(permissions);


        // When - find permission by name
        // When - roles added to repository.
        Permission result = this.testSubject.findByName(PermissionType.SUPER_ADMIN_UPDATE)
                .orElseThrow(() -> new Exception(
                        "Permission with id = " + PermissionType.SUPER_ADMIN_UPDATE + " does not exist."));

        // Then

        // Assertion:
        assertEquals(PermissionType.SUPER_ADMIN_UPDATE, result.getName());

    }

    @Test
    void testFindByName_WithInvalidName() {

        // Given - create list of Permissions
        Permission p0 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_READ)
                .build();
        Permission p1 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_UPDATE)
                .build();
        Permission p2 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_CREATE)
                .build();
        Permission p3 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_DELETE)
                .build();

        List<Permission> permissions = new ArrayList<>();

        permissions.addAll(List.of(p0, p1, p2, p3));

        this.testSubject.saveAll(permissions);

        // When - find permission by invalid name, then exception thrown
        Exception e = assertThrows(Exception.class,
                () -> this.testSubject.findByName(PermissionType.ACCOUNT_ADMIN_READ)
                        .orElseThrow(() -> new Exception(
                                "Permission with name = " + PermissionType.ACCOUNT_ADMIN_READ + " does not exist.")));

    }

}