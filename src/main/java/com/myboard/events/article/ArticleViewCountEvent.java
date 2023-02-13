package com.myboard.events.article;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArticleViewCountEvent {

    private Long articleId;

    public ArticleViewCountEvent(Long articleId) {
        this.articleId = articleId;
    }
}
