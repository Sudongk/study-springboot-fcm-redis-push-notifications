package com.myboard.util.filter;

import com.myboard.entity.User;
import com.myboard.exception.user.UserNotFoundException;
import com.myboard.repository.user.UserRepository;
import com.myboard.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("LoginFilter");
        String username = request.getHeader("username");
        String password = request.getHeader("password");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        response.setHeader(HttpHeaders.AUTHORIZATION, generateToken(authentication));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !StringUtils.startsWithAny(request.getRequestURI(), "/api/v1/login");
    }

    private String generateToken(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String stringRole = user.getAuthorities().stream().findAny().get().getAuthority();

        return jwtProvider.generateToken(user.getUsername(), map_of("roles", stringRole));
    }

    private static Map<String, Object> map_of(String key, Object stringRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(key, stringRole);

        return claims;
    }
}
