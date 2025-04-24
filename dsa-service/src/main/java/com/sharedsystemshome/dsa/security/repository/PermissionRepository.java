package com.sharedsystemshome.dsa.security.repository;

import com.sharedsystemshome.dsa.security.enums.PermissionType;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.model.Permission;
import com.sharedsystemshome.dsa.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository <Permission, Long> {

    Optional<Permission> findByName(PermissionType name);
}
