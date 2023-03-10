package com.myboard.util.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(@Value("${jwt.secret_key}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String sub, Map<String, Object> claims) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(sub)
                .addClaims(claims)
                .setExpiration(Date.from(now.plus(10, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public Map<String, Object> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
