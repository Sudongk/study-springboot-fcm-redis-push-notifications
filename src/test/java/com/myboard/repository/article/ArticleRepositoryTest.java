package com.myboard.repository.article;

import com.myboard.config.TestQuerydslConfig;
import com.myboard.entity.*;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
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
        // given
        Article article = getArticle();

        // when
        Article savedArticle = articleRepository.save(article);

        testEntityManager.flush();

        // then
        assertThat(savedArticle).isSameAs(article);
        assertThat(savedArticle.getTitle()).isNotNull();
        assertThat(savedArticle.getContent()).isNotNull();
        assertThat(savedArticle.getViewCount()).isNotNull();
        assertThat(savedArticle.getUser()).isNotNull();
        assertThat(savedArticle.getBoard()).isNotNull();
    }

    @Test
    @DisplayName("게시글 아이디로 조회 성공")
    void articleFindById() {
        // given
        Article article = getArticle();

        // when
        Article savedArticle = articleRepository.save(article);

        // then
        Optional<Article> findArticle = articleRepository.findById(savedArticle.getId());

        assertThat(findArticle).containsSame(savedArticle);
    }

    @Test
    @DisplayName("게시글에 작성자 아이디 존재 여부 체크(외래키 존재 여부)")
    void checkArticle_userId() {
        // given
        Article article = getArticle();

        Article savedArticle = articleRepository.save(article);

        // when
        Optional<Article> findArticle = articleRepository.findById(savedArticle.getId());

        // then
        assertThat(findArticle).isPresent();
        assertThat(findArticle.get().getUser()).isNotNull();
    }

    @Test
    @DisplayName("게시글에 게시판 아이디 존재 여부 체크(외래키 존재 여부)")
    void checkArticle_boardId() {
        // given
        Article article = getArticle();

        Article savedArticle = articleRepository.save(article);

        // when
        Optional<Article> findArticle = articleRepository.findById(savedArticle.getId());

        // then
        assertThat(findArticle).isPresent();
        assertThat(findArticle.get().getBoard()).isNotNull();
    }

    @Test
    @DisplayName("게시글 아이디로 게시글 리스트 반환")
    void returnArticleListByIds() {
        // given
        List<Article> getArticleList = getArticleList();

        List<Article> articleList = articleRepository.saveAll(getArticleList);

        List<Long> articleIds = articleList.stream()
                .map(BaseColumn::getId)
                .collect(Collectors.toList());

        testEntityManager.clear();

        // when
        List<Article> findArticleList = articleRepository.findAllById(articleIds);

        // then
        assertThat(findArticleList).hasSize(articleList.size());
    }

    @Test
    @DisplayName("사용자 아이디로 게시글 리스트 반환")
    void returnArticleListByUserIds() {
        // given
        List<Article> getArticleList = getArticleList();

        List<Article> articleList = articleRepository.saveAllAndFlush(getArticleList);

        List<Long> userIds = articleList.stream()
                .map(Article::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        testEntityManager.clear();

        // when
        List<Article> findArticleList = articleRepository.findAllByUserIds(userIds);

        // then
        assertThat(findArticleList).hasSize(articleList.size());
    }

    @Test
    @DisplayName("게시판 아이디로 게시글 리스트 반환")
    void returnArticleListByBoardIds() {
        // given
        List<Article> getArticleList = getArticleList();

        List<Article> articleList = articleRepository.saveAllAndFlush(getArticleList);

        List<Long> boardIds = articleList.stream()
                .map(Article::getBoard)
                .map(Board::getId)
                .collect(Collectors.toList());

        testEntityManager.clear();

        // when
        List<Article> findArticleList = articleRepository.findAllByBoardIds(boardIds);

        // then
        assertThat(findArticleList).hasSize(articleList.size());
    }

    @Test
    @DisplayName("특정 게시판 아이디로 게시글 리스트 반환")
    void returnArticleListByBoardId() {
        // given
        List<Article> getArticleList = getArticleList();

        List<Article> articleList = articleRepository.saveAllAndFlush(getArticleList);

        Long boardId = articleList.stream().findFirst().get().getId();

        testEntityManager.clear();

        // when
        List<Article> findArticleList = articleRepository.findAllByBoardId(boardId);

        // then
        assertThat(findArticleList).hasSize(articleList.size());
    }

    @Test
    @DisplayName("사용자 아이디와 게시글 아이디로 게시글 반환")
    void findByUserIdAndArticleId() {
        // given
        Article getArticle = getArticle();

        Article article = articleRepository.save(getArticle);

        Long userId = article.getUser().getId();
        Long articleId = article.getId();

        // when
        Article findArticle = articleRepository.findByUserIdAndArticleId(userId, articleId).get();

        // then
        assertThat(findArticle).isNotNull();
        assertThat(findArticle).isSameAs(article);
    }

    @Test
    @DisplayName("게시글 수정후 반영된 게시글 조회")
    void updateArticle() {
        // given
        Article article = getArticle();

        Article savedArticle = articleRepository.save(article);

        // when
        savedArticle.updateArticleTitle("new title");
        savedArticle.updateArticleContent("new content");

        testEntityManager.flush();
        testEntityManager.clear();

        // then
        Optional<Article> updatedArticle = articleRepository.findById(savedArticle.getId());
        assertThat(updatedArticle).isPresent();
        assertThat(updatedArticle.get().getTitle()).isEqualTo("new title");
        assertThat(updatedArticle.get().getContent()).isEqualTo("new content");
    }

    @Test
    @DisplayName("게시글 삭제후 조회시 존재하지 않음")
    void deleteArticle() {
        // given
        Article article = getArticle();

        Article savedArticle = articleRepository.save(article);

        Long articleId = savedArticle.getId();

        // when
        articleRepository.deleteById(articleId);

        // then
        assertThat(articleRepository.findById(articleId)).isEmpty();
    }

    @Test
    @DisplayName("게시글 조회수 정상 증가")
    void increaseArticleViewCount() {
        // given
        Article article = getArticle();

        Article savedArticle = articleRepository.save(article);

        Long articleId = savedArticle.getId();
        Long viewCount = savedArticle.getViewCount();

        // when
        articleRepository.increaseViewCount(savedArticle.getId());

        testEntityManager.flush();
        testEntityManager.clear();

        // then
        Optional<Article> updatedArticle = articleRepository.findById(articleId);
        assertThat(updatedArticle).isPresent();
        assertThat(updatedArticle.get().getViewCount())
                .isEqualTo(viewCount + 1);
    }
}

