package com.myboard.service.article;

import com.myboard.dto.requestDto.article.CreateArticleDto;
import com.myboard.dto.requestDto.article.UpdateArticleDto;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.entity.Article;
import com.myboard.entity.Board;
import com.myboard.entity.User;
import com.myboard.exception.user.NotAuthorException;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import com.myboard.transactionEvent.article.ArticleViewCountEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ArticleRepository articleRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Long createArticle(CreateArticleDto createArticleDto, Long boardId, Long userId) {
        User userRef = userRepository.getReferenceById(userId);
        Board boardRef = boardRepository.getReferenceById(boardId);

        Article article = Article.builder()
                .title(createArticleDto.getArticleTitle())
                .content(createArticleDto.getArticleContent())
                .board(boardRef)
                .user(userRef)
                .viewCount(0L)
                .build();

        Article savedArticle = articleRepository.save(article);

        return savedArticle.getId();
    }

    @Override
    @Transactional
    public Long updateArticle(UpdateArticleDto updateArticleDto, Long articleId, Long userId) {
        Article article = isExistAndArticleOwnedByUser(articleId, userId);

        article.updateArticleTitle(updateArticleDto.getArticleTitle());
        article.updateArticleContent(updateArticleDto.getArticleContent());

        articleRepository.flush();

        return articleId;
    }

    @Override
    @Transactional
    public Long deleteArticle(Long articleId, Long userId) {
        Article article = isExistAndArticleOwnedByUser(articleId, userId);
        articleRepository.deleteById(article.getId());

        return article.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleResponseDto articleDetail(Long articleId) {
        eventPublisher.publishEvent(new ArticleViewCountEvent(articleId));

        return articleRepository.articleDetail(articleId);
    }

    private Article isExistAndArticleOwnedByUser(Long articleId, Long userId) {
        return articleRepository.findByUserIdAndArticleId(articleId, userId)
                .orElseThrow(NotAuthorException::new);
    }
    
}
