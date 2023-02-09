package com.myboard.repository.board;

import com.myboard.config.TestQuerydslConfig;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.dto.responseDto.tag.TagResponseDto;
import com.myboard.repository.RepositoryExTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false)
@Import(TestQuerydslConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BoardRepositoryExTest extends RepositoryExTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("게시판 리스트 조회시 게시판 리스트용 List<BoardResponseDto> 반환")
    void boardList() {
        // given

        // when
        List<BoardResponseDto> boardResponseDtoList = boardRepository.boardList();

        // then
        assertThat(boardResponseDtoList).hasSize(1);
        assertThat(boardResponseDtoList).extracting(BoardResponseDto::getBoardId)
                .contains(1L);
        assertThat(boardResponseDtoList).extracting(BoardResponseDto::getTotalArticle)
                .contains(2L);
        assertThat(boardResponseDtoList).extracting(BoardResponseDto::getTotalNewArticle)
                .contains(2L);

        for (BoardResponseDto responseDto : boardResponseDtoList) {
            List<TagResponseDto> tags = responseDto.getTags();

            assertThat(tags).extracting(TagResponseDto::getTagName)
                    .contains(Arrays.asList("tag1", "tag2"));
        }

    }

    @Test
    @DisplayName("특정 게시판 조회시 게시판 상세보기용 List<BoardResponseDto> 반환")
    void boardDetail() {
        // given
        Long boardId = 1L;

        // when
        BoardResponseDto boardResponse = boardRepository.boardDetail(boardId);

        // then
        assertThat(boardResponse).extracting(BoardResponseDto::getBoardId)
                .isEqualTo(1L);
        assertThat(boardResponse).extracting(BoardResponseDto::getBoardName)
                .isEqualTo("boardName");

        List<ArticleResponseDto> articleResponseDtoList = boardResponse.getArticleResponseDtoList();
        assertThat(articleResponseDtoList).hasSize(2);
        assertThat(articleResponseDtoList).extracting(ArticleResponseDto::getUsername)
                .contains("test user1");
        assertThat(articleResponseDtoList).extracting(ArticleResponseDto::getArticleId)
                .contains(1L, 2L);
        assertThat(articleResponseDtoList).extracting(ArticleResponseDto::getArticleTitle)
                .contains("title1", "title2");
        assertThat(articleResponseDtoList).extracting(ArticleResponseDto::getViewCount)
                .contains(0L);

    }

}
