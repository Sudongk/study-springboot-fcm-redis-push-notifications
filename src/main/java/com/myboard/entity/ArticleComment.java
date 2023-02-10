package com.myboard.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleComment extends BaseColumn {

    @Column(name = "comment", nullable = false, length = 200)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public ArticleComment(String comment, Article article, User user) {
        this.comment = comment;
        this.article = article;
        this.user = user;
        addCommentToArticle(article);
    }

    private void addCommentToArticle(Article article) {
        if (article != null) {
            article.getArticleCommentList().add(this);
        }
    }

    public void updateArticleComment(String newComment) {
        this.comment = newComment;
    }


}
