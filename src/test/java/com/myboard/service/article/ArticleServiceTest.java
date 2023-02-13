package com.myboard.service.article;

import com.myboard.dto.requestDto.article.CreateArticleDto;
import com.myboard.dto.requestDto.article.UpdateArticleDto;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.entity.*;
import com.myboard.exception.board.BoardNotFoundException;
import com.myboard.exception.user.NotAuthorException;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@DisplayName("게시글 서비스 테스트")
@MockitoSettings
@RecordApplicationEvents
public class ArticleServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long BOARD_ID = 1L;
    private static final Long ARTICLE_ID = 1L;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ApplicationEvents applicationEvents;

    private User user;

    private Board board;

    private Article article;

    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .username("test")
                .password("test")
                .role(User.Role.USER)
                .build();

        this.user.setId(1L);

        this.board = Board.builder()
                .boardName("boardName")
                .user(this.user)
                .build();

        this.board.addTags(Tag.convertListToTags(Arrays.asList("tag1", "tag2")));

        this.article = Article.builder()
                .user(this.user)
                .board(this.board)
                .title("articleTitle")
                .content("articleContent")
                .viewCount(0L)
                .build();
    }

    private ArticleResponseDto articleDetailResponse() {
        return ArticleResponseDto.builder()
                .username("username")
                .articleId(1L)
                .articleTitle("articleTitle")
                .viewCount(0L)
                .createdDateTime(LocalDateTime.now())
                .build();
    }

    private CreateArticleDto getCreateArticleDto() {
        return CreateArticleDto.builder()
                .articleTitle("articleTitle")
                .articleContent("articleContent")
                .build();
    }

    private UpdateArticleDto getUpdateArticleDto() {
        return UpdateArticleDto.builder()
                .articleTitle("newArticleTitle")
                .articleContent("newArticleContent")
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 생성 성공")
    void createArticleSuccessful() throws Exception {
        // given
        CreateArticleDto request = getCreateArticleDto();

        given(userRepository.getReferenceById(USER_ID))
                .willReturn(user);

        given(boardRepository.getReferenceById(BOARD_ID))
                .willReturn(board);

        given(articleRepository.save(any()))
                .willReturn(article);

        // when
        articleService.createArticle(request, BOARD_ID, USER_ID);

        // then
        then(userRepository).should(times(1))
                .getReferenceById(any());

        then(boardRepository).should(times(1))
                .getReferenceById(any());

        then(articleRepository).should(times(1))
                .save(any(Article.class));
    }

    @Test
    @WithMockUser
    @DisplayName("존재하지 않는 유저는 유저일 경우 게시글 생성 실패, 예외 발생")
    void whenUserIsNotExistMustThrowException() throws Exception {
        // given
        CreateArticleDto request = getCreateArticleDto();

        given(userRepository.getReferenceById(USER_ID))
                .willThrow(UsernameNotFoundException.class);

        // when
        assertThatThrownBy(() -> articleService.createArticle(request, BOARD_ID, USER_ID))
                .isInstanceOf(UsernameNotFoundException.class);

        // then
        then(userRepository).should(times(1))
                .getReferenceById(any());

        then(boardRepository).should(times(0))
                .getReferenceById(any());

        then(articleRepository).should(never())
                .save(any(Article.class));
    }

    @Test
    @WithMockUser
    @DisplayName("존재하지 않는 게시판일 경우 게시글 생성 실패, 예외 발생")
    void whenBoardIsNotExistMustThrowException() throws Exception {
        // given
        CreateArticleDto request = getCreateArticleDto();

        given(boardRepository.getReferenceById(BOARD_ID))
                .willThrow(BoardNotFoundException.class);

        // when
        assertThatThrownBy(() -> articleService.createArticle(request, BOARD_ID, USER_ID))
                .isInstanceOf(BoardNotFoundException.class);

        // then
        then(userRepository).should(times(1))
                .getReferenceById(any());

        then(boardRepository).should(times(1))
                .getReferenceById(any());

        then(articleRepository).should(never())
                .save(any(Article.class));
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 수정 성공")
    void updateArticleSuccessful() {
        // given
        UpdateArticleDto request = getUpdateArticleDto();

        given(articleRepository.findByUserIdAndArticleId(ARTICLE_ID, USER_ID))
                .willReturn(Optional.of(article));

        // when
        articleService.updateArticle(request, ARTICLE_ID, USER_ID);

        // then
        then(articleRepository).should(times(1))
                .findByUserIdAndArticleId(ARTICLE_ID, USER_ID);

        then(articleRepository).should(times(1))
                .flush();
    }

    @Test
    @WithMockUser
    @DisplayName("게시글이 해당 유저 소유의 게시글이 아닌 경우 수정 실패 및 예외 발생")
    void whenArticleIsNotBelongsToUserShouldNeverUpdate() {
        // given
        UpdateArticleDto request = getUpdateArticleDto();

        given(articleRepository.findByUserIdAndArticleId(ARTICLE_ID, USER_ID))
                .willThrow(NotAuthorException.class);

        // when
        assertThatThrownBy(() -> articleService.updateArticle(request, ARTICLE_ID, USER_ID))
                .isInstanceOf(NotAuthorException.class);

        // then
        then(articleRepository).should(times(1))
                .findByUserIdAndArticleId(ARTICLE_ID, USER_ID);

        then(articleRepository).should(never())
                .flush();
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 삭제 성공")
    void deleteArticleSuccessful() {
        // given
        given(articleRepository.findByUserIdAndArticleId(ARTICLE_ID, USER_ID))
                .willReturn(Optional.of(article));

        // when
        articleService.deleteArticle(ARTICLE_ID, USER_ID);

        // then
        then(articleRepository).should(times(1))
                .findByUserIdAndArticleId(ARTICLE_ID, USER_ID);

        then(articleRepository).should(times(1))
                .deleteById(any());
    }


    @Test
    @WithMockUser
    @DisplayName("게시글이 해당 유저의 소유가 아닌 경우 삭제 실패 및 예외 발생")
    void whenArticleIsNotBelongsToUserShouldNeverDelete() throws Exception {
        // given
        given(articleRepository.findByUserIdAndArticleId(ARTICLE_ID, USER_ID))
                .willThrow(NotAuthorException.class);

        // when
        assertThatThrownBy(() -> articleService.deleteArticle(ARTICLE_ID, USER_ID))
                .isInstanceOf(NotAuthorException.class);

        // then
        then(articleRepository).should(times(1))
                .findByUserIdAndArticleId(ARTICLE_ID, USER_ID);

        then(articleRepository).should(never())
                .deleteById(any());
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 상세조회 성공")
    void articleDetail() {
        // given
        ArticleResponseDto response = articleDetailResponse();

        given(articleRepository.articleDetail(ARTICLE_ID))
                .willReturn(response);

        // when
        articleService.articleDetail(ARTICLE_ID);

        // then
        then(articleRepository).should(times(1))
                .articleDetail(ARTICLE_ID);
    }
}
