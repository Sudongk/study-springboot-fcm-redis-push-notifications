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
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;

@DataJpaTest(showSql = false)
@Import(TestQuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(value = "classpath:/init.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RepositoryExTest{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    protected User user1, user2;
    protected Board board;
    protected Article article1, article2;
    protected ArticleComment articleComment1, articleComment2;

    @BeforeEach
    void setUp() {
        this.user1 = User.builder()
                .username("test user1")
                .password("password")
                .role(User.Role.USER)
                .build();

//        this.user1.setId(1L);

        this.user2 = User.builder()
                .username("test user2")
                .password("password")
                .role(User.Role.USER)
                .build();

//        this.user2.setId(2L);

        this.board = Board.builder()
                .boardName("boardName")
                .user(this.user1)
                .build();

        this.board.addTags(Tag.convertListToTags(Arrays.asList("tag1, tag2")));
//
//        this.article1 = Article.builder()
//                .title("title1")
//                .content("content1")
//                .viewCount(0L)
//                .user(this.user1)
//                .board(this.board)
//                .build();
//
//        this.article2 = Article.builder()
//                .title("title2")
//                .content("content2")
//                .viewCount(0L)
//                .user(this.user1)
//                .board(this.board)
//                .build();
//
//        this.articleComment1 = ArticleComment.builder()
//                .comment("comment1")
//                .user(this.user2)
//                .article(this.article1)
//                .build();
//
//        this.articleComment2 = ArticleComment.builder()
//                .comment("comment2")
//                .user(this.user2)
//                .article(this.article2)
//                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        boardRepository.save(board);
//
//        articleRepository.save(article1);
//        articleRepository.save(article2);
//
//        articleCommentRepository.save(articleComment1);
//        articleCommentRepository.save(articleComment2);
    }
}

