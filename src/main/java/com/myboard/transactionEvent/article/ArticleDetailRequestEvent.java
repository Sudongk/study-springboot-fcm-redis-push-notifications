package com.myboard.transactionEvent.article;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArticleDetailRequestEvent {

    private Long articleId;

    public ArticleDetailRequestEvent(Long articleId) {
        this.articleId = articleId;
    }
}
