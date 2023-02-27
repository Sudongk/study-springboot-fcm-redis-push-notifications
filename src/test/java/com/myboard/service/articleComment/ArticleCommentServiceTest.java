package com.myboard.service.articleComment;

import com.myboard.dto.requestDto.articleComment.CreateArticleCommentDto;
import com.myboard.dto.requestDto.articleComment.UpdateArticleCommentDto;
import com.myboard.entity.*;
import com.myboard.exception.article.ArticleNotFoundException;
import com.myboard.exception.user.NotAuthorException;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.articleComment.ArticleCommentRepository;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("게시글 댓글 서비스 테스트")
@MockitoSettings
@RecordApplicationEvents
class ArticleCommentServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long BOARD_ID = 1L;
    private static final Long ARTICLE_ID = 1L;
    private static final Long ARTICLE_COMMENT_ID = 1L;

    @InjectMocks
    private ArticleCommentServiceImpl articleCommentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleCommentRepository articleCommentRepository;

    private User user;

    private Board board;

    private Article article;

    private ArticleComment articleComment;

    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .username("username")
                .password("password")
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

        this.articleComment = ArticleComment.builder()
                .comment("articleComment")
                .article(this.article)
                .user(this.user)
                .build();
    }

    private CreateArticleCommentDto getCreateArticleCommentDto() {
        return CreateArticleCommentDto.builder()
                .comment("articleComment")
                .build();
    }

    private UpdateArticleCommentDto getUpdateArticleCommentDto() {
        return UpdateArticleCommentDto.builder()
                .comment("new articleComment")
                .build();
    }

    @Test
    @DisplayName("댓긇 생성 성공")
    void createArticleCommentSuccessful() {
        // given
        CreateArticleCommentDto request = getCreateArticleCommentDto();

        given(userRepository.getReferenceById(USER_ID))
                .willReturn(user);

        given(articleRepository.getReferenceById(ARTICLE_ID))
                .willReturn(article);

        given(articleCommentRepository.save(any()))
                .willReturn(articleComment);

        // when
        articleCommentService.createArticleComment(request, ARTICLE_ID, USER_ID);

        // then
        then(userRepository).should(times(1))
                .getReferenceById(any(Long.class));

        then(articleRepository).should(times(1))
                .getReferenceById(any(Long.class));

        then(articleCommentRepository).should(times(1))
                .save(any(ArticleComment.class));
    }

    @Test
    @DisplayName("존재하지 않는 유저인 경우 댓글 생성 실패")
    void whenUserIsNotExistMustThrowException() {
        // given
        CreateArticleCommentDto request = getCreateArticleCommentDto();

        given(userRepository.getReferenceById(USER_ID))
                .willThrow(UsernameNotFoundException.class);

        // when
        assertThatThrownBy(() -> articleCommentService.createArticleComment(request, ARTICLE_ID, USER_ID))
                .isInstanceOf(UsernameNotFoundException.class);

        // then
        then(userRepository).should(times(1))
                .getReferenceById(any(Long.class));

        then(articleRepository).should(never())
                .getReferenceById(any(Long.class));

        then(articleCommentRepository).should(never())
                .save(any(ArticleComment.class));
    }

    @Test
    @DisplayName("존재하지 않는 게시글인 경우 댓글 생성 실패")
    void whenArticleIsNotExistMustThrowException() {
        // given
        CreateArticleCommentDto request = getCreateArticleCommentDto();

        given(articleRepository.getReferenceById(ARTICLE_ID))
                .willThrow(ArticleNotFoundException.class);

        // when
        assertThatThrownBy(() -> articleCommentService.createArticleComment(request, ARTICLE_ID, USER_ID))
                .isInstanceOf(ArticleNotFoundException.class);

        // then
        then(userRepository).should(times(1))
                .getReferenceById(any(Long.class));

        then(articleRepository).should(times(1))
                .getReferenceById(any(Long.class));

        then(articleCommentRepository).should(never())
                .save(any(ArticleComment.class));
    }

    @Test
    @DisplayName("게시글 댓글 수정 성공")
    void updateArticleCommentSuccessful() {
        // given
        UpdateArticleCommentDto request = getUpdateArticleCommentDto();

        given(articleCommentRepository.findByArticleCommentIdAndUserId(ARTICLE_COMMENT_ID, USER_ID))
                .willReturn(Optional.ofNullable(articleComment));

        // when
        articleCommentService.updateArticleComment(request, ARTICLE_COMMENT_ID, USER_ID);

        // then
        then(articleCommentRepository).should(times(1))
                .findByArticleCommentIdAndUserId(any(Long.class), any(Long.class));

        then(articleCommentRepository).should(times(1))
                .flush();
    }

    @Test
    @DisplayName("게시글 댓글이 해당 유저의 소유가 아닌 경우 수정 실패")
    void whenArticleCommentIsNotBelongsTouUserShouldNeverUpdate() {
        // given
        UpdateArticleCommentDto request = getUpdateArticleCommentDto();

        given(articleCommentRepository.findByArticleCommentIdAndUserId(ARTICLE_COMMENT_ID, USER_ID))
                .willThrow(NotAuthorException.class);

        // when
        assertThatThrownBy(() -> articleCommentService.updateArticleComment(request, ARTICLE_COMMENT_ID, USER_ID))
                .isInstanceOf(NotAuthorException.class);

        // then
        then(articleCommentRepository).should(times(1))
                .findByArticleCommentIdAndUserId(any(Long.class), any(Long.class));

        then(articleCommentRepository).should(never())
                .flush();
    }

    @Test
    @DisplayName("게시글 댓글 삭제 성공")
    void deleteArticleCommentSuccessful() {
        // given
        given(articleCommentRepository.findByArticleCommentIdAndUserId(ARTICLE_COMMENT_ID, USER_ID))
                .willReturn(Optional.ofNullable(articleComment));

        // when
        articleCommentService.deleteArticleComment(ARTICLE_COMMENT_ID, USER_ID);

        // then
        then(articleCommentRepository).should(times(1))
                .findByArticleCommentIdAndUserId(ARTICLE_COMMENT_ID, USER_ID);

        then(articleCommentRepository).should(times(1))
                .deleteById(any());
    }

    @Test
    @DisplayName("게시글 댓글이 해당 유저의 소유가 아닌 삭제 실패")
    void whenArticleCommentIsNotBelongsToUserShouldNeverDelete() {
        // given
        given(articleCommentRepository.findByArticleCommentIdAndUserId(ARTICLE_COMMENT_ID, USER_ID))
                .willThrow(NotAuthorException.class);

        // when
        assertThatThrownBy(() -> articleCommentService.deleteArticleComment(ARTICLE_COMMENT_ID, USER_ID))
                .isInstanceOf(NotAuthorException.class);

        // then
        then(articleCommentRepository).should(times(1))
                .findByArticleCommentIdAndUserId(ARTICLE_COMMENT_ID, USER_ID);

        then(articleCommentRepository).should(never())
                .deleteById(any());
    }
}