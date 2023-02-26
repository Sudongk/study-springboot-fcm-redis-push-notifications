package com.myboard.service.articleComment;

import com.myboard.dto.requestDto.articleComment.CreateArticleCommentDto;
import com.myboard.dto.requestDto.articleComment.UpdateArticleCommentDto;
import com.myboard.entity.*;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.articleComment.ArticleCommentRepository;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
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

    private CreateArticleCommentDto getArticleCommentDto() {
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
        CreateArticleCommentDto request = getArticleCommentDto();

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

}