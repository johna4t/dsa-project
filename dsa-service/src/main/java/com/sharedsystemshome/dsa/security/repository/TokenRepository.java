package com.sharedsystemshome.dsa.security.repository;

import com.sharedsystemshome.dsa.security.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional <Token> findByToken(String token);
    @Query(value = """
      select t from Token t inner join UserAccount u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokensByUser(Long id);

}
