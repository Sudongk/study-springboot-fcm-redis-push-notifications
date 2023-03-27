package com.myboard.service.user;

import com.myboard.dto.requestDto.user.UserCreateRequestDto;
import com.myboard.dto.requestDto.user.UserLoginRequestDto;
import com.myboard.dto.responseDto.user.UserResponseDto;
import com.myboard.entity.User;
import com.myboard.exception.user.SelfConfirmationException;
import com.myboard.exception.user.UserNameDuplicatedException;
import com.myboard.exception.user.UserNotFoundException;
import com.myboard.fcm.FCMTokenManager;
import com.myboard.jwt.JwtTokenManager;
import com.myboard.repository.user.UserRepository;
import com.myboard.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final FCMTokenManager fcmTokenManager;

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
        final String fcmToken = userLoginRequestDto.getFcmToken();

        // 인증
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        User authenticatedUser = (User) authenticate.getPrincipal();
        String jwtToken = jwtProvider.generateToken(authenticatedUser);
        Long authenticatedUserId = authenticatedUser.getId();
        String authenticatedUsername = authenticatedUser.getUsername();

        deleteAndSaveJwtToken(jwtToken, authenticatedUsername);
        deleteAndSaveFCMToken(fcmToken, authenticatedUserId);


        return UserResponseDto.builder()
                .userId(authenticatedUserId)
                .username(authenticatedUsername)
                .token(jwtToken)
                .build();
    }

    // 기존에 존재하는 jwt 삭제
    // Redis에 사용자 이름을 Key로 지정하여 토근값 저장
    private void deleteAndSaveJwtToken(String jwtToken, String authenticatedUsername) {
        jwtTokenManager.deleteAndSaveToken(authenticatedUsername, jwtToken);
    }

    // 기존에 존재하는 Fcm 토큰 삭제
    // Redis에 사용자 아이디를 Key로 Fcm 토큰 저장
    private void deleteAndSaveFCMToken(String fcmToken, Long authenticatedUserId) {
        fcmTokenManager.deleteAndSaveFCMToken(String.valueOf(authenticatedUserId), fcmToken);
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
        userRepository.findById(userId)
                .stream()
                .filter(user -> user.getId().equals(currentUserId))
                .findFirst()
                .orElseThrow(SelfConfirmationException::new);

    }
}