package com.myboard.repository.search;

import com.myboard.dto.requestDto.search.SearchParameter;
import com.myboard.dto.requestDto.search.SearchType;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.dto.responseDto.tag.TagResponseDto;
import com.myboard.repository.SearchRepositoryExTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@Import(SearchRepository.class)
@ActiveProfiles("test")
class SearchRepositoryTest extends SearchRepositoryExTest {

    @Autowired
    private SearchRepository searchRepository;

    @Test
    @DisplayName("게시판 검색 성공 - 결과값에 해당 키워드가 포함되어야 한다")
    void searchBoardFromKeyword() {
        // given
        String keyword = "Spring";
        String type = SearchType.BOARD.getValue();

        SearchParameter searchParameter = SearchParameter.of(keyword, type, "0", "20");
        PageRequest pageRequest = searchParameter.getPageRequest();

        // when
        Page<BoardResponseDto> boardResponseDtoList = searchRepository.searchBoard(searchParameter, pageRequest);
        List<BoardResponseDto> result = boardResponseDtoList.toList();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BoardResponseDto::getBoardName)
                .allMatch(boardName -> boardName.contains("Spring"));
    }

    @Test
    @DisplayName("게시판 검색 성공 - 결과값에 해당 게시판의 태그도 포함되어야 한다")
    void searchFromKeywordMustContainsTags() {
        // given
        String keyword = "Java";
        String type = SearchType.BOARD.getValue();

        SearchParameter searchParameter = SearchParameter.of(keyword, type, "0", "20");
        PageRequest pageRequest = searchParameter.getPageRequest();

        // when
        Page<BoardResponseDto> boardResponseDtoList = searchRepository.searchBoard(searchParameter, pageRequest);
        List<BoardResponseDto> result = boardResponseDtoList.toList();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BoardResponseDto::getBoardName)
                .allMatch(boardName -> boardName.contains("Java"));

        for (BoardResponseDto boardResponseDto : result) {
            List<TagResponseDto> boardTags = boardResponseDto.getTags();

            assertThat(boardTags)
                    .extracting(TagResponseDto::getTagName)
                    .allMatch(tagNames -> tagNames.containsAll(Arrays.asList("java", "it")));
        }
    }

    @Test
    @DisplayName("게시글 검색 성공 - 결과값에 해당 키워드가 포함되어야 한다")
    void searchArticleFromKeyword() {
        // given
        String keyword = "Java";
        String type = SearchType.ARTICLE.getValue();

        SearchParameter searchParameter = SearchParameter.of(keyword, type, "0", "20");
        PageRequest pageRequest = searchParameter.getPageRequest();

        // when
        Page<ArticleResponseDto> articleResponseDtoList = searchRepository.searchArticle(searchParameter, pageRequest);
        List<ArticleResponseDto> result = articleResponseDtoList.toList();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ArticleResponseDto::getArticleTitle)
                .allMatch(articleTitle -> articleTitle.contains("Java"));
    }

    @Test
    @DisplayName("게시글 검색 성공 - 결과값에 해당 게시글에 있는 댓글이 포함되어야 한다")
    void searchArticleFromKeywordMustContainsComments() {
        // given
        String keyword = "IT";
        String type = SearchType.ARTICLE.getValue();

        SearchParameter searchParameter = SearchParameter.of(keyword, type, "0", "20");
        PageRequest pageRequest = searchParameter.getPageRequest();

        // when
        Page<ArticleResponseDto> articleResponseDtoList = searchRepository.searchArticle(searchParameter, pageRequest);
        List<ArticleResponseDto> result = articleResponseDtoList.toList();

        // then
        assertThat(result).hasSize(1);
        assertThat(result).extracting(ArticleResponseDto::getArticleTitle)
                .allMatch(articleTitle -> articleTitle.contains("IT"));

        List<Long> totalArticleComment = result.stream()
                .map(ArticleResponseDto::getTotalArticleComment)
                .collect(Collectors.toList());

        assertThat(totalArticleComment).contains(1L);
    }
}