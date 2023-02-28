package com.myboard.repository.articleComment;

import com.myboard.config.TestQuerydslConfig;
import com.myboard.entity.*;
import com.myboard.repository.article.ArticleRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestQuerydslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleCommentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;

    private Board board;

    private Article article;

    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .username("username")
                .password("password")
                .role(User.Role.USER)
                .build();

        userRepository.save(user);

        this.board = Board.builder()
                .boardName("boardName")
                .user(this.user)
                .build();

        this.board.addTags(Tag.convertListToTags(Arrays.asList("tag1", "tag2")));

        boardRepository.save(board);

        this.article = Article.builder()
                .title("title")
                .content("content")
                .viewCount(0L)
                .user(this.user)
                .board(this.board)
                .build();

        articleRepository.save(article);
    }

    private ArticleComment getArticleComment() {
        return ArticleComment.builder()
                .comment("comment")
                .user(this.user)
                .article(this.article)
                .build();
    }

    private List<ArticleComment> getArticleCommentList() {
        List<ArticleComment> articleCommentList = new ArrayList<>();

        ArticleComment articleComment1 = getArticleComment();
        ArticleComment articleComment2 = getArticleComment();
        ArticleComment articleComment3 = getArticleComment();

        articleCommentList.add(articleComment1);
        articleCommentList.add(articleComment2);
        articleCommentList.add(articleComment3);

        return articleCommentList;
    }

    @Test
    @DisplayName("게시글 댓글 생성 성공")
    void saveArticleComment() {
        // given
        ArticleComment articleComment = getArticleComment();

        // when
        ArticleComment savedArticleComment = articleCommentRepository.save(articleComment);

        testEntityManager.flush();

        // then
        assertThat(savedArticleComment).isSameAs(articleComment);
        assertThat(savedArticleComment.getId()).isNotNull();
        assertThat(savedArticleComment.getComment()).isNotNull();
        assertThat(savedArticleComment.getUser()).isNotNull();
        assertThat(savedArticleComment.getArticle()).isNotNull();
    }

    @Test
    @DisplayName("게시글 댓글 수정 성공")
    void updateArticleComment() {
        // given
        ArticleComment articleComment = getArticleComment();

        ArticleComment savedArticleComment = articleCommentRepository.save(articleComment);

        // when
        savedArticleComment.updateArticleComment("new comment");

        testEntityManager.flush();
        testEntityManager.clear();

        // then
        Optional<ArticleComment> updatedArticleComment = articleCommentRepository.findById(savedArticleComment.getId());
        assertThat(updatedArticleComment).isPresent();
        assertThat(updatedArticleComment.get().getId()).isEqualTo(savedArticleComment.getId());
        assertThat(updatedArticleComment.get().getComment()).isEqualTo("new comment");
    }

    @Test
    @DisplayName("게시글 댓글 삭제 성공 - 삭제후 조회시 존재하지 않음")
    void deleteArticleComment() {
        // given
        ArticleComment articleComment = getArticleComment();

        ArticleComment savedArticleComment = articleCommentRepository.save(articleComment);

        Long articleCommentId = savedArticleComment.getId();

        // when
        articleCommentRepository.deleteById(articleCommentId);

        // then
        assertThat(articleCommentRepository.findById(articleCommentId)).isEmpty();
    }

    @Test
    @DisplayName("게시글 아이디로 댓글 리스트 반환")
    void findArticleCommentListByArticleId() {
        // given
        List<ArticleComment> getArticleCommentList = getArticleCommentList();

        List<ArticleComment> articleCommentList = articleCommentRepository.saveAllAndFlush(getArticleCommentList);

        List<Long> articleIds = articleCommentList.stream()
                .map(ArticleComment::getArticle)
                .map(Article::getId)
                .distinct()
                .collect(Collectors.toList());

        testEntityManager.clear();

        // when
        List<ArticleComment> findArticleComment = articleCommentRepository.findByArticleId(articleIds.get(0));

        // then
        assertThat(findArticleComment).hasSize(articleCommentList.size());
    }

    @Test
    @DisplayName("게시글 아이디와 댓글 아이디로 댓글 조회")
    void findByArticleCommentIdAndUserId() {
        // given
        ArticleComment articleComment = getArticleComment();

        ArticleComment savedArticleComment = articleCommentRepository.save(articleComment);

        Long articleCommentId = savedArticleComment.getId();
        Long articleId = savedArticleComment.getArticle().getId();

        // when
        Optional<ArticleComment> findArticleComment = articleCommentRepository.findByArticleCommentIdAndUserId(articleCommentId, articleId);

        // then
        assertThat(findArticleComment).isPresent();
        assertThat(findArticleComment.get()).isSameAs(savedArticleComment);
    }
}