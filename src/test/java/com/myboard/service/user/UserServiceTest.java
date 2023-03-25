package com.myboard.service.user;

import com.myboard.dto.requestDto.user.UserCreateRequestDto;
import com.myboard.entity.User;
import com.myboard.fcm.FCMTokenManager;
import com.myboard.jwt.JwtTokenManager;
import com.myboard.repository.user.UserRepository;
import com.myboard.util.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
    private JwtProvider jwtProvider;

    @Mock
    private JwtTokenManager jwtTokenManager;

    @Mock
    private FCMTokenManager fcmTokenManager;

    private User user;

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

        User newUser = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(User.Role.USER)
                .build();

        newUser.encodePassword(bCryptPasswordEncoder);

        given(userRepository.findIdByUsername(USER_NAME))
                .willReturn(Optional.empty());

        given(userRepository.save(newUser))
                .willReturn(newUser);

        // when
        userService.userCreate(request);

        // then
        then(userRepository).should(times(1))
                .findIdByUsername(any());

        then(bCryptPasswordEncoder).should(times(1))
                .encode(request.getPassword());

        then(userRepository).should(times(1))
                .save(newUser);
    }
}
