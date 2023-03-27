package com.myboard.service.user;

import com.myboard.dto.requestDto.user.UserCreateRequestDto;
import com.myboard.dto.requestDto.user.UserLoginRequestDto;
import com.myboard.entity.User;
import com.myboard.exception.user.SelfConfirmationException;
import com.myboard.exception.user.UserNameDuplicatedException;
import com.myboard.fcm.FCMTokenManager;
import com.myboard.jwt.JwtTokenManager;
import com.myboard.repository.user.UserRepository;
import com.myboard.util.jwt.JwtProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@DisplayName("사용자 서비스 테스트")
@MockitoSettings
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder bCryptPasswordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private Authentication authentication;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private JwtTokenManager jwtTokenManager;

    @Mock
    private FCMTokenManager fcmTokenManager;

    private User user;

    private static final Long USER_ID = 1L;
    private static final Long CURRENT_USER_ID = 1L;
    private static final Long NOT_EQUALS_CURRENT_USER_ID = 2L;
    private static final String USER_NAME = "username";

    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .username("username")
                .password("password")
                .role(User.Role.USER)
                .build();

        this.user.setId(1L);
    }

    @Test
    @DisplayName("회원가입 성공")
    void createUserSuccessful() {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("username")
                .password("password")
                .build();

        given(userRepository.findIdByUsername(USER_NAME))
                .willReturn(Optional.empty());

        given(userRepository.save(any()))
                .willReturn(user);

        // when
        userService.userCreate(request);

        // then
        then(userRepository).should(times(1))
                .findIdByUsername(any());

        then(bCryptPasswordEncoder).should(times(1))
                .encode(request.getPassword());

        then(userRepository).should(times(1))
                .save(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 회원")
    void whenUserDuplicatedMustThrowException() {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("username")
                .password("password")
                .build();

        given(userRepository.findIdByUsername(USER_NAME))
                .willReturn(Optional.of(USER_ID));

        // when
        assertThatThrownBy(() -> userService.userCreate(request))
                .isInstanceOf(UserNameDuplicatedException.class);

        // then
        then(userRepository).should(times(1))
                .findIdByUsername(USER_NAME);

        then(bCryptPasswordEncoder).should(never())
                .encode(request.getPassword());

        then(userRepository).should(never())
                .save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void authenticateSuccessful() {
        // given
        UserLoginRequestDto request = UserLoginRequestDto.builder()
                .username("username")
                .password("password")
                .build();

        given(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        )).willReturn(authentication);

        given(authentication.getPrincipal()).willReturn(user);

        given(jwtProvider.generateToken(user)).willReturn(any());

        // when
        userService.authenticate(request);

        // then
        then(jwtTokenManager).should(times(1))
                .deleteAndSaveToken(any(), any());

        then(fcmTokenManager).should(times(1))
                .deleteAndSaveFCMToken(any(), any());
    }

    @Test
    @DisplayName("로그인 실패 - 인증실패")
    void authenticateFail() {
        // given
        UserLoginRequestDto request = UserLoginRequestDto.builder()
                .username("username")
                .password("password")
                .build();

        given(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        )).willThrow(BadCredentialsException.class);

        // when
        assertThatThrownBy(() -> userService.authenticate(request))
                .isInstanceOf(BadCredentialsException.class);

        // then
        then(jwtTokenManager).should(never())
                .deleteAndSaveToken(any(), any());

        then(fcmTokenManager).should(never())
                .deleteAndSaveFCMToken(any(), any());
    }

    @Test
    @DisplayName("로그인 실패 - 인증실패")
    void whenUsernameIsNull_authenticate_fail() {
        // given
        UserLoginRequestDto request = UserLoginRequestDto.builder()
                .username(null)
                .password("password")
                .build();

        given(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        )).willThrow(BadCredentialsException.class);

        // when
        assertThatThrownBy(() -> userService.authenticate(request))
                .isInstanceOf(BadCredentialsException.class);

        // then
        then(jwtTokenManager).should(never())
                .deleteAndSaveToken(any(), any());

        then(fcmTokenManager).should(never())
                .deleteAndSaveFCMToken(any(), any());
    }

    @Test
    @DisplayName("로그인 실패 - 인증실패")
    void whenPasswordIsNull_authenticate_fail() {
        // given
        UserLoginRequestDto request = UserLoginRequestDto.builder()
                .username("username")
                .password(null)
                .build();

        given(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        )).willThrow(BadCredentialsException.class);

        // when
        assertThatThrownBy(() -> userService.authenticate(request))
                .isInstanceOf(BadCredentialsException.class);

        // then
        then(jwtTokenManager).should(never())
                .deleteAndSaveToken(any(), any());

        then(fcmTokenManager).should(never())
                .deleteAndSaveFCMToken(any(), any());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void userDelete() {
        // given
        given(userRepository.findById(USER_ID))
                .willReturn(Optional.of(user));

        // when
        userService.userDelete(USER_ID, CURRENT_USER_ID);

        // then
        then(userRepository).should(times(1))
                .findById(USER_ID);

        then(userRepository).should(times(1))
                .deleteById(USER_ID);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - ID 불일치")
    void userDeleteFailWhenIdNotEquals() {
        // given
        given(userRepository.findById(USER_ID))
                .willReturn(Optional.of(user));

        // when
        assertThatThrownBy(() -> userService.userDelete(USER_ID, NOT_EQUALS_CURRENT_USER_ID))
                .isInstanceOf(SelfConfirmationException.class);

        // then
        then(userRepository).should(times(1))
                .findById(USER_ID);

        then(userRepository).should(never())
                .deleteById(USER_ID);
    }
}
