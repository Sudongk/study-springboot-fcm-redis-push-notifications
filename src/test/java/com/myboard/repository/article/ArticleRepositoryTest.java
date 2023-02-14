package com.myboard.repository.article;

import com.myboard.config.TestQuerydslConfig;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.entity.Article;
import com.myboard.entity.Board;
import com.myboard.entity.Tag;
import com.myboard.entity.User;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestQuerydslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;

    private Board board;

    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .username("username")
                .password("password")
                .role(User.Role.USER)
                .build();

        this.user.setId(1L);

        userRepository.save(user);

        this.board = Board.builder()
                .boardName("boardName")
                .user(this.user)
                .build();

        this.board.setId(1L);

        this.board.addTags(Tag.convertListToTags(Arrays.asList("tag1", "tag2")));

        boardRepository.save(board);
    }

    private Article getArticle() {
        return Article.builder()
                .title("article title")
                .content("article content")
                .viewCount(0L)
                .user(this.user)
                .board(this.board)
                .build();
    }

    private List<Article> getArticleList() {
        List<Article> articleList = new ArrayList<>();

        Article article1 = getArticle();
        Article article2 = getArticle();
        Article article3 = getArticle();

        articleList.add(article1);
        articleList.add(article2);
        articleList.add(article3);

        return articleList;
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void articleSave() {

    }
}

