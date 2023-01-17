package com.myboard.controller.board;

import com.google.gson.Gson;
import com.myboard.aop.resolver.CurrentLoginUserIdResolver;
import com.myboard.dto.requestDto.board.CreateBoardDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.repository.user.UserRepository;
import com.myboard.service.board.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.hasSize;


@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

    private static final Long USER_ID = 1L;
    private static final Long BOARD_ID = 1L;

    @InjectMocks
    private BoardController boardController;

    @Mock
    private BoardService boardService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(boardController).build();
    }

    private List<BoardResponseDto> boardListResponse() {

        List<BoardResponseDto> response = new ArrayList<>();

        BoardResponseDto firstBoard = BoardResponseDto.builder()
                .boardId(1L)
                .boardName("test")
                .createdDateTime(LocalDateTime.now())
                .totalArticle(10L)
                .totalNewArticle(1L)
                .build();

        BoardResponseDto secondBoard = BoardResponseDto.builder()
                .boardId(2L)
                .boardName("test")
                .createdDateTime(LocalDateTime.now())
                .totalArticle(10L)
                .totalNewArticle(1L)
                .build();

        response.add(firstBoard);
        response.add(secondBoard);

        return response;
    }

    private CreateBoardDto createBoardRequest() {
        return CreateBoardDto.builder()
                .boardName("test")
                .tagNames(Arrays.asList("test1", "test2"))
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("보드 리스트 보드 리스트 2개를 반환한다.")
    void boardList() throws Exception {
        // given
        List<BoardResponseDto> response = boardListResponse();

        doReturn(response)
                .when(boardService)
                .boardList();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/board")
                        .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                ;
    }



}
