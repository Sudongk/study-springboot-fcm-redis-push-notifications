package com.myboard.service.articleComment;

import com.myboard.dto.requestDto.articleComment.CreateArticleCommentDto;
import com.myboard.dto.requestDto.articleComment.UpdateArticleCommentDto;
import com.myboard.entity.Article;
import com.myboard.entity.ArticleComment;
import com.myboard.entity.User;
import com.myboard.exception.user.NotAuthorException;
import com.myboard.fcm.PushNotificationService;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.articleComment.ArticleCommentRepository;
import com.myboard.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCommentServiceImpl implements ArticleCommentService{

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final PushNotificationService pushNotificationService;

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

        log.info("Fcm start");
        pushNotificationService.commentNotification(articleId, userId);
        log.info("Fcm finish");

        log.info("return ArticleCommentId");
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
        ArticleComment articleComment = isArticleCommentOwnedByUser(articleCommentId, userId);
        articleCommentRepository.deleteById(articleComment.getId());

        return articleComment.getId();
    }

    private ArticleComment isArticleCommentOwnedByUser(Long articleCommentId, Long userId) {
        return articleCommentRepository.findByArticleCommentIdAndUserId(articleCommentId, userId)
                .orElseThrow(NotAuthorException::new);
    }
}
