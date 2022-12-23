package com.myboard.transactionEvent.article;

import com.myboard.repository.article.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleEventHandler {

    private final ArticleRepository articleRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void increaseViewCount(ArticleDetailRequestEvent articleDetailRequestEvent) {
        log.info("event listener start, articleId : {}, thread : {}", articleDetailRequestEvent.getArticleId(), Thread.currentThread());
        articleRepository.increaseViewCount(articleDetailRequestEvent.getArticleId());
    }

}