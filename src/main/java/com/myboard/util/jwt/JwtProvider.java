package com.myboard.util.jwt;

import com.myboard.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(@Value("${jwt.secret_key}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        User user = (User) userDetails;
        log.info("userId: {}", user.getId());
        log.info("username: {}", userDetails.getUsername());
        return Jwts.builder()
                .setClaims(extraClaims)
                .setId(Long.toString(user.getId()))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(key)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        log.info("start generateToken");
        return generateToken(new HashMap<>(), userDetails);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(auth -> auth.startsWith("Bearer "))
                .map(auth -> auth.replace("Bearer ", ""))
                .orElseThrow(() -> new BadCredentialsException("자격 증명에 실패하였습니다."));
    }

    public String getJwtFromRequest(NativeWebRequest webRequest) {
        return Optional.ofNullable(webRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(auth -> auth.startsWith("Bearer "))
                .map(auth -> auth.replace("Bearer ", ""))
                .orElseThrow(() -> new BadCredentialsException("자격 증명에 실패하였습니다."));
    }

    // jwt decode
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
