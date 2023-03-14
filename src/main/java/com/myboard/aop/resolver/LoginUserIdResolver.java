package com.myboard.aop.resolver;

import com.myboard.exception.user.UserNotFoundException;
import com.myboard.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginUserIdResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;
    private final JwtProvider jwtProvider;

    public static final String USER_ID = "userId";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUserId.class) != null &&
                Long.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String token = jwtProvider.getJwtFromRequest(webRequest);

        return Long.valueOf(jwtProvider.extractUserId(token));
    }

}
