package com.myboard.util.filter;

import com.myboard.util.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = getJwtToken(request);

        log.info("jwtFilter doFilterInternal : {}", jwtToken);

        Map<String, Object> claims = jwtTokenProvider.parseClaims(jwtToken);

        SecurityContextHolder.getContext().setAuthentication(createAuthentication(claims));
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean whenLogin = request.getRequestURI().startsWith("/api/v1/login");
        boolean whenSignUp = request.getRequestURI().startsWith("/api/v1/user/create");

        return whenLogin || whenSignUp;
    }

    private String getJwtToken(HttpServletRequest servletRequest) {
        return Optional.ofNullable(servletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(auth -> auth.startsWith("Bearer "))
                .map(auth -> auth.replace("Bearer ", ""))
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 토큰입니다. 다시 로그인"));
    }

    private Authentication createAuthentication(Map<String, Object> claims) {
        List<SimpleGrantedAuthority> roles = Arrays.stream(claims.get("roles").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(claims.get(Claims.SUBJECT), null, roles);
    }
}
