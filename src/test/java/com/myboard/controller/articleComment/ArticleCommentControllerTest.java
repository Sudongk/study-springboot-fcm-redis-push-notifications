package com.myboard.controller.articleComment;

import com.google.gson.Gson;
import com.myboard.aop.resolver.LoginUserIdResolver;
import com.myboard.dto.requestDto.articleComment.CreateArticleCommentDto;
import com.myboard.dto.requestDto.articleComment.UpdateArticleCommentDto;
import com.myboard.exception.GlobalControllerAdvice;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.service.articleComment.ArticleCommentService;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("게시글 댓글 컨트롤러 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ArticleCommentControllerTest {

    @InjectMocks
    private ArticleCommentController articleCommentController;

    @Mock
    private ArticleCommentService articleCommentService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private LoginUserIdResolver loginUserIdResolver;

    private MockMvc mockMvc;

    private static final Long USER_ID = 1L;
    private static final Long BOARD_ID = 1L;
    private static final Long ARTICLE_ID = 1L;
    private static final Long ARTICLE_COMMENT_ID = 1L;

    private static final String USER_ID_AS_STRING = "1";
    private static final String BOARD_ID_AS_STRING = "1";
    private static final String ARTICLE_ID_AS_STRING = "1";
    private static final String ARTICLE_COMMENT_ID_AS_STRING = "1";

    @BeforeEach
    public void init() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(articleCommentController)
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

    @Test
    @DisplayName("게시글 댓글 생성 성공")
    void createArticleCommentSuccessful() throws Exception {
        // given
        CreateArticleCommentDto request = CreateArticleCommentDto.builder()
                .comment("comment")
                .build();

        initCurrentLoginUserIdResolverReturnUserId();

        given(articleCommentService.createArticleComment(request, ARTICLE_ID, USER_ID))
                .willReturn(ARTICLE_COMMENT_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/articleComment/create/{articleId}", ARTICLE_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        MvcResult response = resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        then(articleCommentService).should(times(1))
                .createArticleComment(any(), any(), any());

        assertThat(response.getResponse().getContentAsString())
                .isEqualTo(ARTICLE_COMMENT_ID_AS_STRING);
    }

    @Test
    @DisplayName("게시긇 댓글 내용이 null인 경우 예외 발생")
    void whenCommentIsNullShouldThrowException() throws Exception {
        // given
        CreateArticleCommentDto request = CreateArticleCommentDto.builder()
                .comment(null)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/articleComment/create/{articleId}", ARTICLE_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("C002")))
                .andExpect(jsonPath("$.message", comparesEqualTo("댓글 내용은 필수 입력값입니다.")))
        ;

        then(articleCommentService).should(never())
                .createArticleComment(any(), any(), any());

    }

    @Test
    @DisplayName("게시긇 댓글 내용이 공백인 경우 예외 발생")
    void whenCommentIsBlankShouldThrowException() throws Exception {
        // given
        CreateArticleCommentDto request = CreateArticleCommentDto.builder()
                .comment(" ")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/articleComment/create/{articleId}", ARTICLE_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("C002")))
                .andExpect(jsonPath("$.message", comparesEqualTo("댓글 내용은 필수 입력값입니다.")))
        ;

        then(articleCommentService).should(never())
                .createArticleComment(any(), any(), any());
    }

    @Test
    @DisplayName("게시글 댓글 내용이 최대 글자수 초과인 경우 예외 발생")
    void whenCommentIsExceedLengthShouldThrowException() throws Exception {
        // given
        String comment = RandomStringUtils.randomAlphabetic(300);

        CreateArticleCommentDto request = CreateArticleCommentDto.builder()
                .comment(comment)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/articleComment/create/{articleId}", ARTICLE_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("C001")))
                .andExpect(jsonPath("$.message", comparesEqualTo("댓글은 200자 이하여야 합니다.")))
        ;

        then(articleCommentService).should(never())
                .createArticleComment(any(), any(), any());
    }

    @Test
    @DisplayName("게시글 댓글 수정 성공")
    void updateArticleCommentSuccessful() throws Exception {
        // given
        UpdateArticleCommentDto request = UpdateArticleCommentDto.builder()
                .comment("comment")
                .build();

        initCurrentLoginUserIdResolverReturnUserId();

        given(articleCommentService.updateArticleComment(request, ARTICLE_ID, USER_ID))
                .willReturn(ARTICLE_COMMENT_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/articleComment/update/{articleCommentId}", ARTICLE_COMMENT_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        MvcResult response = resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        then(articleCommentService).should(times(1))
                .updateArticleComment(any(), any(), any());

        assertThat(response.getResponse().getContentAsString())
                .isEqualTo(ARTICLE_COMMENT_ID_AS_STRING);
    }

    @Test
    @DisplayName("변경할 게시글 댓글 내용이 null인 경우 예외 발생")
    void whenUpdateCommentIsNullShouldThrowException() throws Exception {
        // given
        UpdateArticleCommentDto request = UpdateArticleCommentDto.builder()
                .comment(null)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/articleComment/update/{articleCommentId}", ARTICLE_COMMENT_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("C002")))
                .andExpect(jsonPath("$.message", comparesEqualTo("댓글 내용은 필수 입력값입니다.")))
        ;

        then(articleCommentService).should(never())
                .updateArticleComment(any(), any(), any());
    }

    @Test
    @DisplayName("변경할 게시글 댓글 내용이 최대 글자수 초과인 경우 예외 발생")
    void whenUpdateCommentIsExceedLengthShouldThrowException() throws Exception {
        // given
        String comment = RandomStringUtils.randomAlphabetic(300);

        UpdateArticleCommentDto request = UpdateArticleCommentDto.builder()
                .comment(comment)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/articleComment/update/{articleId}", ARTICLE_COMMENT_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .characterEncoding("UTF-8")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code", comparesEqualTo("C001")))
                .andExpect(jsonPath("$.message", comparesEqualTo("댓글은 200자 이하여야 합니다.")))
        ;

        then(articleCommentService).should(never())
                .updateArticleComment(any(), any(), any());
    }

    @Test
    @DisplayName("게시글 댓글 삭제 성공")
    void deleteArticleCommentSuccessful() throws Exception {
        // given
        initCurrentLoginUserIdResolverReturnUserId();

        given(articleCommentService.deleteArticleComment(ARTICLE_COMMENT_ID, ARTICLE_ID))
                .willReturn(ARTICLE_COMMENT_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/articleComment/delete/{articleCommentId}", ARTICLE_COMMENT_ID)
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
                .isEqualTo(ARTICLE_COMMENT_ID_AS_STRING);
    }
}