package com.myboard.service.search;

import com.myboard.dto.requestDto.search.SearchParameter;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.repository.search.SearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.mockito.BDDMockito.*;

@DisplayName("검색 서비스 테스트")
@MockitoSettings
public class SearchServiceTest {

    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private SearchRepository searchRepository;

    @Test
    @DisplayName("검색타입이 게시판이면 게시판 검색")
    void boardSearch() {
        // given
        String keyword = "Java";
        String searchType = "board";
        SearchParameter searchParameter = SearchParameter.of(keyword, searchType, "0", "20");
        PageRequest pageRequest = searchParameter.ofPageRequest();

        Page<BoardResponseDto> mockPage = mock(Page.class);

        given(searchRepository.searchBoard(any(SearchParameter.class), eq(pageRequest)))
                .willReturn(mockPage);

        // when
        searchService.search(searchParameter);

        // then
        then(searchRepository).should(times(1))
                .searchBoard(any(), any());
    }

    @Test
    @DisplayName("검색타입이 게시글이면 게시글 검색")
    void articleSearch() {
        // given
        String keyword = "Java";
        String searchType = "article";
        SearchParameter searchParameter = SearchParameter.of(keyword, searchType, "0", "20");
        PageRequest pageRequest = searchParameter.ofPageRequest();

        Page<ArticleResponseDto> mockPage = mock(Page.class);

        given(searchRepository.searchArticle(any(SearchParameter.class), eq(pageRequest)))
                .willReturn(mockPage);

        // when
        searchService.search(searchParameter);

        // then
        then(searchRepository).should(times(1))
                .searchArticle(any(), any());
    }
}
