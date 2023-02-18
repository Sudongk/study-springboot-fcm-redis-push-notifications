package com.myboard.controller.search;

import com.myboard.aop.resolver.SearchParamsArgumentResolver;
import com.myboard.dto.requestDto.search.SearchParameter;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.dto.responseDto.search.SearchResponseDto;
import com.myboard.exception.GlobalControllerAdvice;
import com.myboard.exception.search.InvalidPageStartException;
import com.myboard.service.search.SearchService;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("검색 컨트롤러 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class SearchControllerTest {

    @InjectMocks
    private SearchController searchController;

    @Mock
    private SearchService searchService;

    @Mock
    private SearchParamsArgumentResolver searchParamsArgumentResolver;

    private MockMvc mockMvc;

    private final static String BOARD_KEYWORD = "board";
    private final static String ARTICLE_KEYWORD = "article";
    private final static String TYPE_BOARD = "board";
    private final static String TYPE_ARTICLE = "article";
    private final static String START = "0";
    private final static String SIZE = "20";


    @BeforeEach
    public void init() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setControllerAdvice(new GlobalControllerAdvice())
                .setCustomArgumentResolvers(searchParamsArgumentResolver)
                .build();
    }

    private void initSearchParamsArgumentResolverBoardType() throws Exception {
        given(searchParamsArgumentResolver.supportsParameter(any()))
                .willReturn(true);

        given(searchParamsArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(SearchParameter.of(
                        BOARD_KEYWORD, TYPE_BOARD, START, SIZE
                ));
    }

    private void initSearchParamsArgumentResolverArticleType() throws Exception {
        given(searchParamsArgumentResolver.supportsParameter(any()))
                .willReturn(true);

        given(searchParamsArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(SearchParameter.of(
                        ARTICLE_KEYWORD, TYPE_ARTICLE, START, SIZE
                ));
    }

    private BoardResponseDto getBoardResponseDto() {
        return BoardResponseDto.builder()
                .boardId(1L)
                .boardName("board name")
                .createdDateTime(LocalDateTime.now())
                .totalArticle(2L)
                .totalNewArticle(1L)
                .build();
    }

    private ArticleResponseDto getArticleResponseDto() {
        return ArticleResponseDto.builder()
                .username("username")
                .articleId(1L)
                .articleTitle("article title")
                .viewCount(0L)
                .createdDateTime(LocalDateTime.now())
                .build();
    }

    private SearchResponseDto getBoardTypeSearchResponseDto() {
        SearchResponseDto searchResponseDto = new SearchResponseDto();
        searchResponseDto.setResult(getBoardResponseDto());

        return searchResponseDto;
    }

    private SearchResponseDto getArticleTypeSearchResponseDto() {
        SearchResponseDto searchResponseDto = new SearchResponseDto();
        searchResponseDto.setResult(getArticleResponseDto());

        return searchResponseDto;
    }

    @Test
    @WithMockUser
    @DisplayName("타입: 게시판 검색 - 성공")
    void searchBoard() throws Exception {
        // given
        SearchResponseDto searchResponse = getBoardTypeSearchResponseDto();

        SearchParameter searchParameter = SearchParameter.of(BOARD_KEYWORD, TYPE_BOARD, START, SIZE);

        initSearchParamsArgumentResolverBoardType();

        given(searchService.search(searchParameter))
                .willReturn(searchResponse);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .param("keyword", BOARD_KEYWORD)
                        .param("type", TYPE_BOARD)
                        .param("start", START)
                        .param("size", SIZE)
        );

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.boardId", searchResponse).exists())
                .andExpect(jsonPath("$.result.boardName", searchResponse).exists())
                .andExpect(jsonPath("$.result.createdDateTime", searchResponse).exists())
                .andExpect(jsonPath("$.result.totalArticle", searchResponse).exists())
                .andExpect(jsonPath("$.result.totalNewArticle", searchResponse).exists())
        ;
    }

    @Test
    @WithMockUser
    @DisplayName("타입: 게시글 검색 - 성공")
    void searchArticle() throws Exception {
        // given
        SearchResponseDto searchResponse = getArticleTypeSearchResponseDto();

        SearchParameter searchParameter = SearchParameter.of(ARTICLE_KEYWORD, TYPE_ARTICLE, START, SIZE);

        initSearchParamsArgumentResolverArticleType();

        given(searchService.search(searchParameter))
                .willReturn(searchResponse);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .param("keyword", ARTICLE_KEYWORD)
                        .param("type", TYPE_ARTICLE)
                        .param("start", START)
                        .param("size", SIZE)
        );

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.articleId", searchResponse).exists())
                .andExpect(jsonPath("$.result.username", searchResponse).exists())
                .andExpect(jsonPath("$.result.createdDateTime", searchResponse).exists())
                .andExpect(jsonPath("$.result.articleTitle", searchResponse).exists())
                .andExpect(jsonPath("$.result.viewCount", searchResponse).exists())
        ;
    }
}
