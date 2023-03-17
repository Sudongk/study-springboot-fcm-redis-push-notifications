package com.myboard.config.security;

import com.myboard.firebase.fcm.FcmTokenManager;
import com.myboard.jwt.JwtTokenManager;
import com.myboard.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class LogoutHandlerImpl implements LogoutHandler {

    private final JwtProvider jwtProvider;
    private final JwtTokenManager jwtTokenManager;
    private final FcmTokenManager fcmTokenManager;

    // 로그아웃시 jwt, fcm 토큰 삭제
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = jwtProvider.getJwtFromRequest(request);
        String username = jwtProvider.extractUsername(token);
        String userId = jwtProvider.extractUserId(token);

        jwtTokenManager.removeToken(username);
        fcmTokenManager.removeToken(String.valueOf(userId));
    }
}
