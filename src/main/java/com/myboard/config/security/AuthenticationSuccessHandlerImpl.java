package com.myboard.config.security;

import com.myboard.config.firebase.FirebaseTokenManager;
import com.myboard.exception.user.UserNotFoundException;
import com.myboard.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component("customAuthenticationSuccessHandler")
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final static String USER_ID = "userId";
    private final UserRepository userRepository;
    private final HttpSession httpSession;
    private final FirebaseTokenManager firebaseTokenManager;

    // session에 userId 저장, redis에 토근 저장
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("AuthenticationSuccessHandlerImpl");
    }
}
