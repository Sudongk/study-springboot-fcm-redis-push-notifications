package com.myboard.service.articleComment;

import com.myboard.dto.requestDto.articleComment.CreateArticleCommentDto;
import com.myboard.dto.requestDto.articleComment.UpdateArticleCommentDto;
import com.myboard.entity.Article;
import com.myboard.entity.ArticleComment;
import com.myboard.entity.User;
import com.myboard.exception.articleComment.CommentNotFoundException;
import com.myboard.exception.user.NotAuthorException;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.articleComment.ArticleCommentRepository;
import com.myboard.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ArticleCommentServiceImpl implements ArticleCommentService{

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Override
    @Transactional
    public Long createArticleComment(CreateArticleCommentDto createArticleCommentDto, Long articleId, Long userId) {
        User userRef = userRepository.getReferenceById(userId);
        Article articleRef = articleRepository.getReferenceById(articleId);

        ArticleComment articleComment = ArticleComment.builder()
                .comment(createArticleCommentDto.getComment())
                .article(articleRef)
                .user(userRef)
                .build();

        ArticleComment savedArticleComment = articleCommentRepository.save(articleComment);

        return savedArticleComment.getId();
    }

    @Override
    @Transactional
    public Long updateArticleComment(UpdateArticleCommentDto updateArticleCommentDto, Long articleCommentId, Long userId) {
        ArticleComment articleComment = isArticleCommentOwnedByUser(articleCommentId, userId);

        articleComment.updateArticleComment(updateArticleCommentDto.getComment());

        articleCommentRepository.flush();

        return articleCommentId;
    }

    @Override
    @Transactional
    public Long deleteArticleComment(Long articleCommentId, Long userId) {
        isArticleCommentOwnedByUser(articleCommentId, userId);
        articleCommentRepository.deleteById(articleCommentId);

        return articleCommentId;
    }

    private ArticleComment isArticleCommentOwnedByUser(Long articleCommentId, Long userId) {
        return articleCommentRepository.findIdByUserIdAndArticleCommentId(articleCommentId, userId)
                .orElseThrow(NotAuthorException::new);
    }
}
