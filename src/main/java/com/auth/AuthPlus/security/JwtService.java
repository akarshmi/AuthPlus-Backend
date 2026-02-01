package com.auth.AuthPlus.security;


import com.auth.AuthPlus.entities.Role;
import com.auth.AuthPlus.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Getter
@Setter
public class JwtService
{

    private final SecretKey secretKey;
    private final long accessTokenTTL;
    private final long refreshTokenTTL;
    private final String issuer;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-ttl}") long accessTokenTTL,
            @Value("${security.jwt.refresh-token-ttl}") long refreshTokenTTL,
            @Value("${security.jwt.issuer}") String issuer )
    {
        if (secret == null || secret.isEmpty() || secret.length() < 64) {
            throw new IllegalArgumentException("Invalid Secret!!!");
        }

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTTL = accessTokenTTL;
        this.refreshTokenTTL = refreshTokenTTL;
        this.issuer = issuer;

    }

    //Generate Token;
    public String generateAccessToken(User user){
        Instant now = Instant.now();
        List<String> roles = user.getRoles() == null ? List.of() :
                user.getRoles().stream().map(Role::getName).toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUserId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenTTL)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "type", "access"
                ))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // generate refresh tokens
    public String generateRefreshToken(User user, String jti){
        Instant now = Instant.now();
        return Jwts.builder()
                .id(jti)
                .subject(user.getUserId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenTTL)))
                .claims(Map.of("type", "refresh"))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    //parse token
    public Jws<Claims> parseToken(String token){
//        try{
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
//        }
//        catch (Exception e){
//            throw new InvalidTokenException("Invalid Token!!!");
//        }
    }

    public boolean isAccessToken(String token){
        Claims c = parseToken(token).getPayload();
        return "access".equals(c.get("type"));
    }
    public boolean isRefreshToken(String token){
        Claims c = parseToken(token).getPayload();
        return "refresh".equals(c.get("type"));
    }

    public UUID getUserId(String token){
        Claims c = parseToken(token).getPayload();
        return UUID.fromString(c.getSubject());
    }

    public String getJti(String token){
        return parseToken(token).getPayload().getId();
    }

    public List<String> getRoles(String token){
        Claims c = parseToken(token).getPayload();
        return (List<String>) c.get("roles");
    }

    public  String getEmail(String token){
        Claims c = parseToken(token).getPayload();
        return (String) c.get("email");
    }
}
