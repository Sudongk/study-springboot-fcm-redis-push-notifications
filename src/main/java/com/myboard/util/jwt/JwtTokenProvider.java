package com.myboard.util.jwt;

import com.myboard.util.key.RsaKey;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RsaKey key;

    public String generateToken(String sub, Map<String, Object> claims) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(sub)
                .addClaims(claims)
                .setExpiration(Date.from(now.plus(1, ChronoUnit.DAYS)))
                .signWith(key.getPrivateKey())
                .compact();
    }

    public Map<String, Object> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key.getPrivateKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
