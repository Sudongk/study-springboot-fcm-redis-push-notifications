package com.myboard.repository;

import com.myboard.config.TestQuerydslConfig;
import com.myboard.entity.*;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.articleComment.ArticleCommentRepository;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

@DataJpaTest(showSql = false)
@Import(TestQuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SearchRepositoryExTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    protected User user1, user2;
    protected Board board1, board2, board3, board4;
    protected Article article1, article2, article3, article4;
    protected ArticleComment articleComment1, articleComment2, articleComment3, articleComment4;

    @BeforeEach
    void setUp() {
        initUser();
        initBoard();
        initArticle();
        initArticleComment();
    }

    private void initUser() {
        this.user1 = User.builder()
                .username("user1")
                .password("password")
                .role(User.Role.USER)
                .build();

        this.user2 = User.builder()
                .username("user2")
                .password("password")
                .role(User.Role.USER)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
    }

    private void initBoard() {
        this.board1 = Board.builder()
                .boardName("Java")
                .user(this.user1)
                .build();

        this.board1.addTags(Tag.convertListToTags(Arrays.asList("java", "it")));

        this.board2 = Board.builder()
                .boardName("Spring")
                .user(this.user2)
                .build();

        this.board2.addTags(Tag.convertListToTags(Arrays.asList("trip", "car")));

        this.board3 = Board.builder()
                .boardName("I Love Java")
                .user(this.user1)
                .build();

        this.board3.addTags(Tag.convertListToTags(Arrays.asList("java", "it")));

        this.board4 = Board.builder()
                .boardName("I Love Spring")
                .user(this.user2)
                .build();

        this.board4.addTags(Tag.convertListToTags(Arrays.asList("spring")));

        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);
        boardRepository.save(board4);
    }

    private void initArticle() {
        this.article1 = Article.builder()
                .title("title1")
                .content("content1")
                .viewCount(0L)
                .user(this.user1)
                .board(this.board1)
                .build();

        this.article2 = Article.builder()
                .title("title2")
                .content("content2")
                .viewCount(0L)
                .user(this.user1)
                .board(this.board1)
                .build();

        this.article3 = Article.builder()
                .title("title3")
                .content("content3")
                .viewCount(0L)
                .user(this.user2)
                .board(this.board2)
                .build();

        this.article4 = Article.builder()
                .title("title4")
                .content("content4")
                .viewCount(0L)
                .user(this.user2)
                .board(this.board2)
                .build();

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(article3);
        articleRepository.save(article4);
    }

    private void initArticleComment() {
        this.articleComment1 = ArticleComment.builder()
                .comment("comment1")
                .user(this.user2)
                .article(this.article1)
                .build();

        this.articleComment2 = ArticleComment.builder()
                .comment("comment2")
                .user(this.user2)
                .article(this.article2)
                .build();

        this.articleComment3 = ArticleComment.builder()
                .comment("comment3")
                .user(this.user1)
                .article(this.article3)
                .build();

        this.articleComment4 = ArticleComment.builder()
                .comment("comment4")
                .user(this.user1)
                .article(this.article4)
                .build();

        articleCommentRepository.save(articleComment1);
        articleCommentRepository.save(articleComment2);
        articleCommentRepository.save(articleComment3);
        articleCommentRepository.save(articleComment4);
    }
}
