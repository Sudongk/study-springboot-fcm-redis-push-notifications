package com.myboard.service.user;

import com.myboard.dto.requestDto.user.UserCreateRequestDto;
import com.myboard.dto.requestDto.user.UserLoginRequestDto;
import com.myboard.dto.responseDto.user.UserResponseDto;
import com.myboard.entity.User;
import com.myboard.exception.user.SelfConfirmationException;
import com.myboard.exception.user.UserNameDuplicatedException;
import com.myboard.exception.user.UserNotFoundException;
import com.myboard.jwt.JwtTokenManager;
import com.myboard.repository.user.UserRepository;
import com.myboard.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final JwtTokenManager jwtTokenManager;
    private final HttpSession httpSession;

    @Override
    @Transactional
    public Long userCreate(UserCreateRequestDto userCreateRequestDto) {
        isUserNameDuplicated(userCreateRequestDto.getUsername());

        User newUser = User.builder()
                .username(userCreateRequestDto.getUsername())
                .password(userCreateRequestDto.getPassword())
                .role(User.Role.USER)
                .build();

        newUser.encodePassword(bCryptPasswordEncoder);

        User savedUser = userRepository.save(newUser);

        return savedUser.getId();
    }

    @Override
    @Transactional
    public Long userDelete(Long userId, Long currentUserId) {
        isUserIdOwnedByCurrentUser(userId, currentUserId);
        userRepository.deleteById(userId);
        return userId;
    }

    // login
    @Override
    public UserResponseDto authenticate(UserLoginRequestDto userLoginRequestDto) {
        final String username = userLoginRequestDto.getUsername();
        final String password = userLoginRequestDto.getPassword();

        // 인증
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        User user = (User) authenticate.getPrincipal();
        String token = jwtProvider.generateToken(user);

        // 기존에 존재하는 토큰 삭제
        Optional.ofNullable(jwtTokenManager.getToken(username))
                .ifPresent(
                        e -> jwtTokenManager.removeToken(username)
                );

        // Redis에 사용자 이름을 Key로 지정하여 토근값 저장
        jwtTokenManager.saveToken(user.getUsername(), token);

        httpSession.setAttribute("USER_ID", user.getId());

        return UserResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .token(token)
                .build();
    }

    private User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    private void isUserNameDuplicated(String username) {
        Optional<Long> userId = userRepository.findIdByUsername(username);

        if (userId.isPresent()) {
            throw new UserNameDuplicatedException();
        }
    }

    private void isUserIdOwnedByCurrentUser(Long userId, Long currentUserId) {
        if (!Objects.equals(currentUserId, userId)) {
            throw new SelfConfirmationException();
        }
    }
}