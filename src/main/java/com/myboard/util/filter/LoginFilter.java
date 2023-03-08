package com.myboard.util.filter;

import com.myboard.exception.user.UserNotFoundException;
import com.myboard.repository.user.UserRepository;
import com.myboard.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = request.getHeader("username");
        String password = request.getHeader("password");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        response.setHeader(HttpHeaders.AUTHORIZATION, generateToken(authentication));

        Long userId = userRepository.findIdByUsername(String.valueOf(username))
                .orElseThrow(UserNotFoundException::new);

        httpSession.setAttribute("USER_ID", userId);
        httpSession.setMaxInactiveInterval(600);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        boolean isLogin = HttpMethod.POST.matches(method) && requestURI.startsWith("/api/v1/login");

        return !isLogin;
    }

    private String generateToken(Authentication authentication) {
        User user = (User)authentication.getPrincipal();

        log.info("generateToken : {}", user.toString());

        String stringRole = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        return jwtProvider.generateToken(user.getUsername(), map_of("roles", stringRole));
    }

    private static Map<String, Object> map_of(String key, Object stringRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(key, stringRole);
        return claims;
    }

}
