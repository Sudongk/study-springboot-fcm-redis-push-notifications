package com.myboard.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("customLogoutHandler")
public class LogoutHandlerImpl implements LogoutHandler {



    // session에서 userId 삭제, redis에서 토근 삭제
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        
    }
}
