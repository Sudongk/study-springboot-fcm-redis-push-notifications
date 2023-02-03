package com.myboard.controller.board;

import com.google.gson.Gson;
import com.myboard.aop.resolver.CurrentLoginUserIdResolver;
import com.myboard.dto.requestDto.board.CreateBoardDto;
import com.myboard.dto.requestDto.board.UpdateBoardDto;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.exception.GlobalControllerAdvice;
import com.myboard.service.board.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.hasSize;


@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

    private static final Long USER_ID = 1L;
    private static final Long BOARD_ID = 1L;
    private static final String USER_ID_STRING = "1";
    private static final String BOARD_ID_STRING = "1";
    private static final Long MAX_BOARD_NAME_LENGTH = 30L;
    private static final Long MAX_TAG_NAME_LENGTH = 20L;

    @InjectMocks
    private BoardController boardController;

    @Mock
    private BoardService boardService;

    @Mock
    private CurrentLoginUserIdResolver currentLoginUserIdResolver;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(boardController)
                .setCustomArgumentResolvers(currentLoginUserIdResolver)
                .setControllerAdvice(new GlobalControllerAdvice())
                .build();
    }

    private void initCurrentLoginUserIdResolverReturnUserId() throws Exception {
        // init CurrentUserLoginResolver
        given(currentLoginUserIdResolver.supportsParameter(any()))
                .willReturn(true);

        given(currentLoginUserIdResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(USER_ID);
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

    private BoardResponseDto boardDetailResponse() {
        List<ArticleResponseDto> articleResponseList = new ArrayList<>();

        ArticleResponseDto firstArticle = ArticleResponseDto.builder()
                .username("test")
                .articleId(1L)
                .articleTitle("test")
                .viewCount(1L)
                .createdDateTime(LocalDateTime.now())
                .build();

        ArticleResponseDto secondArticle = ArticleResponseDto.builder()
                .username("test2")
                .articleId(2L)
                .articleTitle("test2")
                .viewCount(2L)
                .createdDateTime(LocalDateTime.now())
                .build();

        articleResponseList.add(firstArticle);
        articleResponseList.add(secondArticle);

        BoardResponseDto response = BoardResponseDto.builder()
                .boardId(1L)
                .boardName("test")
                .createdDateTime(LocalDateTime.now())
                .build();

        response.setArticleResponseDtoList(articleResponseList);

        return response;
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 전체 조회시 게시판 리스트 2개를 반환한다.")
    void boardList() throws Exception {
        // given
        List<BoardResponseDto> response = boardListResponse();

        given(boardService.boardList())
                .willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/board")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        then(boardService).should().boardList();
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 생성이 성공적일 때, 생성된 게시판 id값 반환")
    void whenBoardCreationSuccessful_MustReturnLong() throws Exception {
        // given
        CreateBoardDto request = CreateBoardDto.builder()
                .boardName("test")
                .tagNames(Arrays.asList("test1", "test2"))
                .build();

        initCurrentLoginUserIdResolverReturnUserId();

        given(boardService.createBoard(request, USER_ID))
                .willReturn(BOARD_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/board/create")
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
                .isEqualTo(USER_ID_STRING);
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 이름이 null 이라면 예외 발생")
    void whenBoardNameIsNull_MustThrowException() throws Exception {
        // given
        CreateBoardDto request = CreateBoardDto.builder()
                .boardName(null)
                .tagNames(Arrays.asList("test1", "test2"))
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/board/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        //then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 이름이 공백이라면 예외 발생")
    void whenBoardNameIsBlank_MustThrowException() throws Exception {
        // given
        CreateBoardDto request = CreateBoardDto.builder()
                .boardName(" ")
                .tagNames(Arrays.asList("test1", "test2"))
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/board/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        //then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 이름의 길이가 30자 초과하면 예외 발생")
    void whenBoardNameLengthExceed_MustThrowException() throws Exception {
        // given
        String boardName = "testNametestNametestNametestNametestNametestName";

        CreateBoardDto request = CreateBoardDto.builder()
                .boardName(boardName)
                .tagNames(Arrays.asList("test1", "test2"))
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/board/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 생성시 게시판 태그가 1개 이상 설정 안했을시 예외 발생")
    void whenTagNameIsNull_MustThrowException() throws Exception {
        // given
        CreateBoardDto request = CreateBoardDto.builder()
                .boardName("test")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/board/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 수정 성공시 변경된 게시판 ID 리턴")
    void boardUpdate() throws Exception {
        // given
        UpdateBoardDto request = UpdateBoardDto.builder()
                .boardName("test")
                .tagNames(Arrays.asList("test1", "test2"))
                .build();

        initCurrentLoginUserIdResolverReturnUserId();

        given(boardService.updateBoard(request, BOARD_ID, USER_ID))
                .willReturn(BOARD_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/board/{boardId}/update", BOARD_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        MvcResult response = resultActions
                .andExpect(status().isOk())
                .andReturn();

        assertThat(response.getResponse().getContentAsString())
                .isEqualTo(BOARD_ID_STRING);
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 삭제시 삭제된 게시판 ID 반환")
    void deleteBoard() throws Exception {
        // given
        initCurrentLoginUserIdResolverReturnUserId();

        given(boardService.deleteBoard(BOARD_ID, USER_ID))
                .willReturn(BOARD_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/board/{boardId}/delete", BOARD_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        // then
        MvcResult response = resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertThat(response.getResponse().getContentAsString())
                .isEqualTo(BOARD_ID_STRING);
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 상세보기시 해당 게시판에 속한 게시글 리스트 반환")
    void boardDetail() throws Exception {
        // given
        BoardResponseDto result = boardDetailResponse();

        given(boardService.boardDetail(BOARD_ID))
                .willReturn(result);

        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/board/{boardId}", BOARD_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId", result.getBoardId()).exists())
                .andExpect(jsonPath("$.boardName", result.getBoardName()).exists())
                .andExpect(jsonPath("$.createdDateTime", result.getCreatedDateTime()).exists())
                .andExpect(jsonPath("$.articleResponseDtoList", hasSize(2)))
        ;
    }
}
