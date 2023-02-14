package com.myboard.events;

import com.myboard.entity.Article;
import com.myboard.entity.Board;
import com.myboard.entity.Tag;
import com.myboard.entity.User;
import com.myboard.events.article.ArticleViewCountEvent;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import com.myboard.service.article.ArticleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@RecordApplicationEvents
@ActiveProfiles("test")
@Transactional
public class ArticleViewCountEventTest {

    private static final Long ARTICLE_ID = 1L;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private Board board;
    private Article article;

    @BeforeEach
    void setUp() {
        initUser();
        initBoard();
        initArticle();
    }

    private void initUser() {
        this.user = User.builder()
                .username("test user1")
                .password("password")
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
    }

    private void initBoard() {
        this.board = Board.builder()
                .boardName("boardName")
                .user(this.user)
                .build();

        this.board.addTags(Tag.convertListToTags(Arrays.asList("tag1", "tag2")));

        boardRepository.save(board);
    }

    private void initArticle() {
        this.article = Article.builder()
                .title("title1")
                .content("content1")
                .viewCount(0L)
                .user(this.user)
                .board(this.board)
                .build();

        articleRepository.save(article);
    }

    @Test
    @DisplayName("게시글 조회수 증가 비동기 이벤트 테스트")
    void articleViewCountAsyncEvent() throws Exception {
        // given
        Article article = articleRepository.findAll().stream().findAny().get();

        // when
        articleService.articleDetail(article.getId());

        // then
        long count = applicationEvents.stream(ArticleViewCountEvent.class).count();
        assertThat(count).isEqualTo(1L);
    }
}
