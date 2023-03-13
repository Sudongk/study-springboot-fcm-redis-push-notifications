package com.myboard.config.security;

import com.myboard.jwt.JwtTokenManager;
import com.myboard.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LogoutHandlerImpl implements LogoutHandler {

    private final JwtProvider jwtProvider;
    private final JwtTokenManager jwtTokenManager;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = jwtProvider.getJwtFromRequest(request);
        String username = jwtProvider.extractUsername(token);

        Optional.ofNullable(jwtTokenManager.getToken(username))
                .ifPresent(
                        e -> jwtTokenManager.removeToken(username)
                );
    }
}
