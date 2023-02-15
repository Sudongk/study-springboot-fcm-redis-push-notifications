package com.myboard.repository.article;

import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.articleComment.ArticleCommentResponseDto;
import com.myboard.entity.Article;
import com.myboard.entity.ArticleComment;
import com.myboard.entity.BaseColumn;
import com.myboard.entity.User;
import com.myboard.repository.RepositoryExTest;
import com.myboard.repository.articleComment.ArticleCommentRepository;
import com.myboard.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
public class ArticleRepositoryExTest extends RepositoryExTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    @Test
    @DisplayName("게시글 상세조회시 게시글 상세조회용 ArticleResponseDto 반환")
    void articleDetail() {
        // given
        Article article = articleRepository.findAll().stream().findAny().get();

        User user = userRepository.findById(article.getUser().getId()).get();

        List<ArticleComment> articleComments = articleCommentRepository.findByArticleId(article.getId());

        String username = user.getUsername();
        Long articleId = article.getId();
        String articleTitle = article.getTitle();
        String articleContent = article.getContent();
        Long viewCount = article.getViewCount();
        int totalArticleComment = articleComments.size();

        List<String> usernamesInComments = articleComments.stream()
                .map(ArticleComment::getUser)
                .map(User::getUsername)
                .collect(Collectors.toList());

        List<Long> articleCommentIds = articleComments.stream()
                .map(BaseColumn::getId)
                .collect(Collectors.toList());

        List<String> comments = articleComments.stream()
                .map(ArticleComment::getComment)
                .collect(Collectors.toList());

        // when
        ArticleResponseDto articleResponseDto = articleRepository.articleDetail(article.getId());

        // then
        assertThat(articleResponseDto).isNotNull();
        assertThat(articleResponseDto.getUsername()).isEqualTo(username);
        assertThat(articleResponseDto.getArticleId()).isEqualTo(articleId);
        assertThat(articleResponseDto.getArticleTitle()).isEqualTo(articleTitle);
        assertThat(articleResponseDto.getArticleContent()).isEqualTo(articleContent);
        assertThat(articleResponseDto.getViewCount()).isEqualTo(viewCount);

        List<ArticleCommentResponseDto> articleCommentsDtoList = articleResponseDto.getArticleComments();

        assertThat(articleCommentsDtoList).hasSize(totalArticleComment);

        assertThat(articleCommentsDtoList).extracting(ArticleCommentResponseDto::getUsername)
                .contains(usernamesInComments.get(0));

        assertThat(articleCommentsDtoList).extracting(ArticleCommentResponseDto::getArticleCommentId)
                .contains(articleCommentIds.get(0));

        assertThat(articleCommentsDtoList).extracting(ArticleCommentResponseDto::getComment)
                .contains(comments.get(0));
    }
}
