package com.myboard.controller.articleComment;

import com.google.gson.Gson;
import com.myboard.aop.resolver.CurrentLoginUserIdResolver;
import com.myboard.dto.requestDto.articleComment.CreateArticleCommentDto;
import com.myboard.exception.GlobalControllerAdvice;
import com.myboard.service.articleComment.ArticleCommentService;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private CurrentLoginUserIdResolver currentLoginUserIdResolver;

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

        then(articleCommentService).should(never()).createArticleComment(any(), any(), any());

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

        then(articleCommentService).should(never()).createArticleComment(any(), any(), any());
    }

    @Test
    @DisplayName("게시긇 댓글 내용이 최대 글자수 초과인 경우 예외 발생")
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

        then(articleCommentService).should(never()).createArticleComment(any(), any(), any());
    }
}