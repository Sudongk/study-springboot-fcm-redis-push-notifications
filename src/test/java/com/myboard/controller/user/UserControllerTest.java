package com.myboard.controller.user;

import com.google.gson.Gson;
import com.myboard.aop.resolver.LoginUserIdResolver;
import com.myboard.dto.requestDto.user.UserCreateRequestDto;
import com.myboard.dto.requestDto.user.UserLoginRequestDto;
import com.myboard.dto.responseDto.user.UserResponseDto;
import com.myboard.exception.GlobalControllerAdvice;
import com.myboard.service.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("사용자 컨트롤러 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private LoginUserIdResolver loginUserIdResolver;

    private MockMvc mockMvc;

    private static final Long USER_ID = 1L;
    private static final String USER_ID_AS_STRING = "1";

    @BeforeEach
    public void init() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(loginUserIdResolver)
                .setControllerAdvice(new GlobalControllerAdvice())
                .build();
    }

    private void initCurrentLoginUserIdResolverReturnUserId() throws Exception {
        // init CurrentUserLoginResolver
        given(loginUserIdResolver.supportsParameter(any()))
                .willReturn(true);

        given(loginUserIdResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(USER_ID);
    }

    private static String randomStringGenerator(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Test
    @DisplayName("회원가입 성공 = 생성된 사용자 ID 반환")
    void createUserSuccessful() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("username")
                .password("password")
                .build();

        given(userService.userCreate(request))
                .willReturn(USER_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/user/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        MvcResult response = resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(response.getResponse().getContentAsString())
                .isEqualTo(USER_ID_AS_STRING);
    }

    @Test
    @DisplayName("회원명이 null이면 회원가입 실패")
    void whenUsernameIsNullMustThrowException() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username(null)
                .password("password")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/user/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("U003")))
                .andExpect(jsonPath("$.message", comparesEqualTo("회원명은 필수 입력값입니다.")))
        ;

        then(userService).should(never()).userCreate(request);
    }

    @Test
    @DisplayName("회원명이 공백이면 회원가입 실패")
    void whenUsernameIsBlankMustThrowException() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username(" ")
                .password("password")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/user/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("U003")))
                .andExpect(jsonPath("$.message", comparesEqualTo("회원명은 필수 입력값입니다.")))
        ;

        then(userService).should(never()).userCreate(request);
    }

    @Test
    @DisplayName("회원명이 최대 글자수 초과시 회원가입 실패")
    void whenUsernameIsToLongMustThrowException() throws Exception {
        // given
        String username = randomStringGenerator(100);

        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username(username)
                .password("password")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/user/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("U004")))
                .andExpect(jsonPath("$.message", comparesEqualTo("이름은 최소 1자 이상, 최대 20자까지 입력 가능합니다.")))
        ;

        then(userService).should(never()).userCreate(request);
    }

    @Test
    @DisplayName("비밀번호가 null이면 회원가입 실패")
    void whenPasswordIsNullMustThrowException() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("username")
                .password(null)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/user/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("U007")))
                .andExpect(jsonPath("$.message", comparesEqualTo("비밀번호는 필수 입력값입니다.")))
        ;

        then(userService).should(never()).userCreate(request);
    }

    @Test
    @DisplayName("비밀번호가 공백이면 회원가입 실패")
    void whenPasswordIsBlankMustThrowException() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("username")
                .password(" ")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/user/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("U007")))
                .andExpect(jsonPath("$.message", comparesEqualTo("비밀번호는 필수 입력값입니다.")))
        ;

        then(userService).should(never()).userCreate(request);
    }

    @Test
    @DisplayName("비밀번호 최대 글자수 초과시 회원가입 실패")
    void whenPasswordIsToLongMustThrowException() throws Exception {
        // given
        String password = randomStringGenerator(100);

        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("username")
                .password(password)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/user/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("U008")))
                .andExpect(jsonPath("$.message", comparesEqualTo("비밀번호는 최소 8자 이상, 최대 20자까지 입력 가능합니다.")))
        ;

        then(userService).should(never()).userCreate(request);
    }

    @Test
    @DisplayName("회원탈퇴 성공 - 삭제된 사용사 ID 반환")
    void deleteUserSuccessful() throws Exception {
        // given
        initCurrentLoginUserIdResolverReturnUserId();

        given(userService.userDelete(USER_ID, USER_ID))
                .willReturn(USER_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/user/delete/{userId}", USER_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        assertThat(
                resultActions
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .isEqualTo(USER_ID_AS_STRING);
    }

    @Test
    @DisplayName("로그인 생공 - UserResponseDto 반환")
    void userLoginSuccessful() throws Exception {
        // given
        UserLoginRequestDto request = UserLoginRequestDto.builder()
                .username("username")
                .password("password")
                .fcmToken("fcmToken")
                .build();

        UserResponseDto expectedResponse = UserResponseDto.builder()
                .userId(USER_ID)
                .username("username")
                .token("token")
                .build();

        given(userService.authenticate(request))
                .willReturn(expectedResponse);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/user/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(expectedResponse.getUserId()))
                .andExpect(jsonPath("$.username", comparesEqualTo(expectedResponse.getUsername())))
                .andExpect(jsonPath("$.token", comparesEqualTo(expectedResponse.getToken())));

    }
}
