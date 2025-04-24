package com.sharedsystemshome.dsa.security.service;

import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.security.enums.TokenType;
import com.sharedsystemshome.dsa.security.model.Token;
import com.sharedsystemshome.dsa.security.repository.TokenRepository;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.sharedsystemshome.dsa.util.BusinessValidationException.CUSTOMER_ACCOUNT;
import static com.sharedsystemshome.dsa.util.BusinessValidationException.TOKEN;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${sharedsystemshome.dsa.secret-key}")
    private String secretKey;

    @Value("${sharedsystemshome.dsa.jwt.expiration}")
    private Long jwtExpiration;

    @Value("${sharedsystemshome.dsa.refresh.expiration}")
    private Long refreshExpiration;

    private final TokenRepository tokenRepo;

    public String extractJwtUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractCustomerAccountId(String token) {
        return extractClaim(token, claims -> claims.get("customerAccountId", Long.class));
    }

    public Token generateAccessToken(UserAccount user) {

        Token access =  buildToken(new HashMap<>(), user, jwtExpiration);

        // Token already set to default TokenType.ACCESS;

        return access;
    }

    public Token generateRefreshToken(UserAccount user) {

        Token refresh = buildToken(new HashMap<>(), user, refreshExpiration);

        // Change Token from default TokenType.ACCESS to TokenType.REFRESH;
        refresh.setTokenType(TokenType.REFRESH);

        return refresh;
    }

    private Token buildToken(Map<String, Object> extraClaims, UserAccount user, long expiration) {
        Date now = new Date(System.currentTimeMillis());
        Date expiry = new Date(now.getTime() + expiration);

        Long customerAccountId = user.getParentAccount() != null ? user.getParentAccount().getId() : null;
        if (customerAccountId != null) {
            extraClaims.put("customerAccountId", customerAccountId);
        } else {
            throw new SecurityValidationException("Unable to create " + TOKEN + " claim with null " + CUSTOMER_ACCOUNT + " id.");
        }

        String tokenString = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        return Token.builder()
                .token(tokenString)
                .user(user)
                .build();
    }

    public Boolean isTokenValid(String token, UserDetails subject) {
        final String jwtUsername = extractJwtUserName(token);

        if (!jwtUsername.equals(subject.getUsername()) || isTokenExpired(token)) {
            return false;
        }

        Optional<Token> storedToken = tokenRepo.findByToken(token);
        return storedToken != null && !storedToken.get().getExpired() && !storedToken.get().getRevoked();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secretKey));
    }
}
