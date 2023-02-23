package com.myboard.repository.search;

import com.myboard.dto.requestDto.search.SearchParameter;
import com.myboard.dto.requestDto.search.SearchType;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.dto.responseDto.tag.TagResponseDto;
import com.myboard.entity.Tag;
import com.myboard.repository.SearchRepositoryExTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Import(SearchRepository.class)
@ActiveProfiles("test")
class SearchRepositoryTest extends SearchRepositoryExTest {

    @Autowired
    private SearchRepository searchRepository;

    @Test
    @DisplayName("게시판 검색 성공 - 결과값에 해당 키워드가 포함되어야 한다")
    void searchFromKeyword() {
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
}