package com.myboard.repository.board;

import com.myboard.config.TestQuerydslConfig;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.dto.responseDto.tag.TagResponseDto;
import com.myboard.entity.Article;
import com.myboard.entity.Board;
import com.myboard.entity.Tag;
import com.myboard.entity.User;
import com.myboard.repository.RepositoryExTest;
import com.myboard.repository.article.ArticleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
public class BoardRepositoryExTest extends RepositoryExTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("게시판 리스트 조회시 게시판 리스트용 List<BoardResponseDto> 반환")
    void boardList() {
        // given
        Board board = boardRepository.findAll().stream().findAny().get();

        List<String> boardTags = board.getTags().stream().map(Tag::getName).collect(Collectors.toList());

        List<Article> articles = articleRepository.findByBoardId(board.getId());

        Long size = Long.valueOf(articles.size());

        // when
        List<BoardResponseDto> boardResponseDtoList = boardRepository.boardList();

        // then
        assertThat(boardResponseDtoList).hasSize(1);

        assertThat(boardResponseDtoList).extracting(BoardResponseDto::getBoardId)
                .contains(board.getId());

        assertThat(boardResponseDtoList).extracting(BoardResponseDto::getTotalArticle)
                .contains(size);

        assertThat(boardResponseDtoList).extracting(BoardResponseDto::getTotalNewArticle)
                .contains(size);

        for (BoardResponseDto responseDto : boardResponseDtoList) {
            List<TagResponseDto> tags = responseDto.getTags();

            assertThat(tags).extracting(TagResponseDto::getTagName)
                    .contains(boardTags);
        }

    }

    @Test
    @DisplayName("특정 게시판 조회시 게시판 상세보기용 List<BoardResponseDto> 반환")
    void boardDetail() {
        // given
        Board board = boardRepository.findAll().stream().findAny().get();

        List<Article> articles = articleRepository.findAll();

        List<Long> articleIds = articles.stream().map(Article::getId).collect(Collectors.toList());

        List<String> articleTitles = articles.stream().map(Article::getTitle).collect(Collectors.toList());

        List<String> usernames = articles.stream().map(Article::getUser).map(User::getUsername).collect(Collectors.toList());

        List<Long> viewCounts = articles.stream().map(Article::getViewCount).collect(Collectors.toList());

        // when
        BoardResponseDto boardResponse = boardRepository.boardDetail(board.getId());

        // then
        assertThat(boardResponse).extracting(BoardResponseDto::getBoardId)
                .isEqualTo(board.getId());

        assertThat(boardResponse).extracting(BoardResponseDto::getBoardName)
                .isEqualTo(board.getBoardName());

        List<ArticleResponseDto> articleResponseDtoList = boardResponse.getArticleResponseDtoList();

        assertThat(articleResponseDtoList).hasSize(articles.size());

        assertThat(articleResponseDtoList).extracting(ArticleResponseDto::getUsername)
                .contains(usernames.get(0), usernames.get(1));

        assertThat(articleResponseDtoList).extracting(ArticleResponseDto::getArticleId)
                .contains(articleIds.get(0), articleIds.get(1));

        assertThat(articleResponseDtoList).extracting(ArticleResponseDto::getArticleTitle)
                .contains(articleTitles.get(0), articleTitles.get(0));

        assertThat(articleResponseDtoList).extracting(ArticleResponseDto::getViewCount)
                .contains(viewCounts.get(0), viewCounts.get(1));

    }

}
