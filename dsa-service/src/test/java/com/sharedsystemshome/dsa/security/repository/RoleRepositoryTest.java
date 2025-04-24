package com.sharedsystemshome.dsa.security.repository;

import com.sharedsystemshome.dsa.security.enums.PermissionType;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.model.Permission;
import com.sharedsystemshome.dsa.security.model.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public
class RoleRepositoryTest {

    @Autowired
    RoleRepository testSubject;

    @Autowired
    PermissionRepository permissionRepo;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSaveAll() {

        // Given - create list of Permissions
        Permission p0 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_READ)
                .build();
        Permission p1 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_UPDATE)
                .build();
        Permission p2 = Permission.builder()
                .name(PermissionType.MEMBER_READ)
                .build();
        Permission p3 = Permission.builder()
                .name(PermissionType.MEMBER_CREATE)
                .build();

        List<Permission> permissions = new ArrayList<>();

        permissions.addAll(List.of(p0, p1, p2, p3));

        this.permissionRepo.saveAll(permissions);


        Role role0 = Role.builder()
                .name(RoleType.SUPER_ADMIN)
                .permissions(List.of(p0, p1))
                .build();

        Role role1 = Role.builder()
                .name(RoleType.MEMBER)
                .permissions(List.of(p2, p3))
                .build();

        // When - roles added to repository.
        this.testSubject.saveAll(List.of(role0, role1));

        // Then

        // Assertion: repository should contain two roles.
        assertEquals(2, this.testSubject.count());
    }

    @Test
    void testFindByName() throws Exception{

        // Given - create list of Permissions
        Permission p0 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_READ)
                .build();
        Permission p1 = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_UPDATE)
                .build();
        Permission p2 = Permission.builder()
                .name(PermissionType.MEMBER_READ)
                .build();
        Permission p3 = Permission.builder()
                .name(PermissionType.MEMBER_CREATE)
                .build();

        this.permissionRepo.saveAll(List.of(p0, p1, p2, p3));

        Role role0 = Role.builder()
                .name(RoleType.SUPER_ADMIN)
                .permissions(List.of(p0, p1))
                .build();

        Role role1 = Role.builder()
                .name(RoleType.MEMBER)
                .permissions(List.of(p2, p3))
                .build();

        this.testSubject.saveAll(List.of(role0, role1));

        // When - roles added to repository.
        Role result = this.testSubject.findByName(RoleType.MEMBER)
                .orElseThrow(() -> new Exception(
                        "DataSharingParty with id = " + RoleType.MEMBER + " does not exist."));

        // Then

        // Assertion:
        assertEquals(RoleType.MEMBER, result.getName());

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
                .name(PermissionType.MEMBER_READ)
                .build();
        Permission p3 = Permission.builder()
                .name(PermissionType.MEMBER_CREATE)
                .build();

        Role role0 = Role.builder()
                .name(RoleType.SUPER_ADMIN)
                .permissions(List.of(p0, p1))
                .build();

        Role role1 = Role.builder()
                .name(RoleType.MEMBER)
                .permissions(List.of(p2, p3))
                .build();

        this.testSubject.saveAll(List.of(role0, role1));

        // When - find role by invalid name, then exception thrown
        Exception e = assertThrows(Exception.class,
                () -> this.testSubject.findByName(RoleType.USER)
                        .orElseThrow(() -> new Exception(
                                "Role with name = " + RoleType.USER + " does not exist.")));

    }

}