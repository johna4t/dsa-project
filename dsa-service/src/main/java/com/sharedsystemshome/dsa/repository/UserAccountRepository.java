package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);

    Optional<List<UserAccount>> findUserAccountByParentAccountId(Long custId);

    Optional<UserAccount> findByIdAndParentAccountId(Long userId, Long customerId);

    Boolean existsByIdAndParentAccountId(Long userId, Long customerId);

    Boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM UserAccount u JOIN u.roles r " +
            "WHERE u.parentAccount.id = :parentAccountId AND r.name = :roleName")
    Long countByRoleNameAndParentAccountId(@Param("roleName") RoleType roleName,
                                       @Param("parentAccountId") Long parentAccountId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserAccount u WHERE u.id = :id")
    void deleteUserAccountById(@Param("id") Long id);

}
