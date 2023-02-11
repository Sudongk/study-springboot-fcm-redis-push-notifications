package com.myboard.controller.article;

import com.google.gson.Gson;
import com.myboard.aop.resolver.CurrentLoginUserIdResolver;
import com.myboard.dto.requestDto.article.CreateArticleDto;
import com.myboard.dto.requestDto.article.UpdateArticleDto;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.exception.GlobalControllerAdvice;
import com.myboard.service.article.ArticleService;

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

import javax.validation.ConstraintViolationException;
import javax.xml.bind.ValidationException;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("게시글 컨트롤러 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class ArticleControllerTest {

    @InjectMocks
    private ArticleController articleController;

    @Mock
    private ArticleService articleService;

    @Mock
    private CurrentLoginUserIdResolver currentLoginUserIdResolver;

    private MockMvc mockMvc;

    private static final Long USER_ID = 1L;
    private static final Long BOARD_ID = 1L;
    private static final Long ARTICLE_ID = 1L;
    private static final String USER_ID_AS_STRING = "1";
    private static final String BOARD_ID_AS_STRING = "1";
    private static final String ARTICLE_ID_AS_STRING = "1";

    @BeforeEach
    public void init() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(articleController)
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

    private ArticleResponseDto getArticleResponseDto() {
        return ArticleResponseDto.builder()
                .username("username")
                .articleId(1L)
                .articleTitle("title")
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 생성 성공 - 생성된 게시글 Id 반환")
    void createArticleSuccessful() throws Exception {
        // given
        CreateArticleDto request = CreateArticleDto.builder()
                .articleTitle("title")
                .articleContent("content")
                .build();

        initCurrentLoginUserIdResolverReturnUserId();

        given(articleService.createArticle(request, BOARD_ID, USER_ID))
                .willReturn(ARTICLE_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/article/create/{boardId}", BOARD_ID)
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
                .isEqualTo(ARTICLE_ID_AS_STRING);

    }

    @Test
    @WithMockUser
    @DisplayName("게시글 생성 실패 - 게시글 제목 null - 예외 코드, 메시지 검증")
    void whenArticleTitleIsNullMustThrowException() throws Exception {
        // given
        CreateArticleDto request = CreateArticleDto.builder()
                .articleTitle(null)
                .articleContent("content")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/article/create/{boardId}", BOARD_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("A002")))
                .andExpect(jsonPath("$.message", comparesEqualTo("게시글 제목은 필수 입력값입니다.")))
        ;

        then(articleService).should(never()).createArticle(request, BOARD_ID, USER_ID);
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 생성 실패 - 게시글 내용 null - 예외 코드, 메시지 검증")
    void whenArticleContentIsNullMustThrowException() throws Exception {
        // given
        CreateArticleDto request = CreateArticleDto.builder()
                .articleTitle("title")
                .articleContent(null)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/article/create/{boardId}", BOARD_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("A003")))
                .andExpect(jsonPath("$.message", comparesEqualTo("게시글 내용은 필수 입력값입니다.")))
        ;

        then(articleService).should(never()).createArticle(request, BOARD_ID, USER_ID);
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 수정 성공 - 수정된 게시글 ID 반환")
    void updateArticleSuccessful() throws Exception {
        // given
        UpdateArticleDto request = UpdateArticleDto.builder()
                .articleTitle("newArticleTitle")
                .articleContent("newArticleContent")
                .build();

        initCurrentLoginUserIdResolverReturnUserId();

        given(articleService.updateArticle(request, ARTICLE_ID, USER_ID))
                .willReturn(ARTICLE_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/article/{articleId}/update", ARTICLE_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        assertThat(
                resultActions
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .isEqualTo(ARTICLE_ID_AS_STRING);

    }

    @Test
    @WithMockUser
    @DisplayName("게시글 수정 실패 -변경할 게시글 제목 null - 예외, 메세지 검증")
    void whenNewTitleIsNullMustThrowException() throws Exception {
        // given
        UpdateArticleDto request = UpdateArticleDto.builder()
                .articleTitle(null)
                .articleContent("newArticleContent")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/article/{articleId}/update", ARTICLE_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("A002")))
                .andExpect(jsonPath("$.message", comparesEqualTo("게시글 제목은 필수 입력값입니다.")))
        ;

        then(articleService).should(never()).updateArticle(request, ARTICLE_ID, USER_ID);
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 수정 실패 -변경할 게시글 내용 null - 예외, 메세지 검증")
    void whenNewContentIsNullMustThrowException() throws Exception {
        // given
        UpdateArticleDto request = UpdateArticleDto.builder()
                .articleTitle("newTitle")
                .articleContent(null)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/article/{articleId}/update", ARTICLE_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("A003")))
                .andExpect(jsonPath("$.message", comparesEqualTo("게시글 내용은 필수 입력값입니다.")))
        ;

        then(articleService).should(never()).updateArticle(request, ARTICLE_ID, USER_ID);
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 삭제 성공 - 삭제된 게시글 ID 반환")
    void deleteArticleSuccessful() throws Exception {
        // given
        initCurrentLoginUserIdResolverReturnUserId();

        given(articleService.deleteArticle(ARTICLE_ID, USER_ID))
                .willReturn(ARTICLE_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/article/{articleId}/delete", ARTICLE_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        // then
        assertThat(
                resultActions
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .isEqualTo(ARTICLE_ID_AS_STRING);
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 상세 조회")
    void articleDetail() throws Exception {
        // given
        ArticleResponseDto response = getArticleResponseDto();

        given(articleService.articleDetail(ARTICLE_ID))
                .willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/article/{articleId}", ARTICLE_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", response.getUsername()).exists())
                .andExpect(jsonPath("$.articleId", response.getArticleId()).exists())
                .andExpect(jsonPath("$.articleTitle", response.getArticleTitle()).exists())
        ;

        then(articleService).should().articleDetail(ARTICLE_ID);
    }
}
