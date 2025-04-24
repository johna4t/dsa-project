package com.sharedsystemshome.dsa.security.service;

import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.security.repository.TokenRepository;
import com.sharedsystemshome.dsa.security.model.Token;
import com.sharedsystemshome.dsa.service.UserAccountService;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.util.CustomValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.sharedsystemshome.dsa.util.BusinessValidationException.*;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepo;

    private final UserAccountRepository userRepo;

    private final UserContextService userContextService;

    private final CustomValidator<Token> validator;

    private static final Logger logger = LoggerFactory.getLogger(UserAccountService.class);

    public Long createToken(Token token){

        if(null == token){
            throw new SecurityValidationException(TOKEN + " is null or empty.");
        }

        this.validator.validate(token);

        try{
            return this.tokenRepo.save(token).getId();
        } catch(Exception e){
            throw new SecurityValidationException("Unable to add or update " + TOKEN + ".");
        }
    }

    public List<Token> getTokens(){
        return this.tokenRepo.findAll();
    }

    public void revokeUserTokens(UserAccount user) {

        if(null == user){
            throw new SecurityValidationException(USER_ACCOUNT + " is null or empty.");
        }

        String email = user.getEmail();
        if(!this.userRepo.existsByEmail(email)){
            throw new SecurityValidationException(USER_ACCOUNT + " with email = " + email + " not found.");
        }

        List<Token> userTokens = this.tokenRepo.findAllValidTokensByUser(user.getId());

        if (userTokens.isEmpty()) {
            return;
        }

        UserAccount currentUser = null;
        try {
            currentUser = this.userContextService.getCurrentUser();
        } catch(Exception e){
            // No authenticated user currently exists, so leave as null
        }

        final UserAccount finalCurrentUser = currentUser;

        userTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            token.setRevokedAt(LocalDateTime.now());
            token.setRevokedBy(finalCurrentUser);
        });

        this.tokenRepo.saveAll(userTokens);
    }

    public void revokeUserToken(String tokenValue){

        UserAccount currentUser = this.userContextService.getCurrentUser();

        this.tokenRepo.findByToken(tokenValue).ifPresent(t -> {
            t.setExpired(true);
            t.setRevoked(true);
            t.setRevokedAt(LocalDateTime.now());
            t.setRevokedBy(currentUser != null ? currentUser : null);
            this.tokenRepo.save(t);
            logger.info("Revoked token {} for user {}",
                    t.getToken(),
                    currentUser != null ? currentUser.getEmail() : "unknown");
        });
    }

    public Token getTokenByValue(String tokenValue) {

        Optional<Token> token = this.tokenRepo.findByToken(tokenValue);

        return token.get();

    }

}
