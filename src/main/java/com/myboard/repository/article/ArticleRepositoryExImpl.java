package com.myboard.repository.article;

import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.article.QArticleResponseDto;
import com.myboard.dto.responseDto.articleComment.ArticleCommentResponseDto;
import com.myboard.dto.responseDto.articleComment.QArticleCommentResponseDto;
import com.myboard.entity.QArticle;
import com.myboard.entity.QArticleComment;
import com.myboard.entity.QUser;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.myboard.entity.QArticle.*;
import static com.myboard.entity.QArticleComment.*;
import static com.myboard.entity.QUser.*;
import static com.querydsl.core.group.GroupBy.*;

public class ArticleRepositoryExImpl implements ArticleRepositoryEx{

    private final JPAQueryFactory queryFactory;

    public ArticleRepositoryExImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ArticleResponseDto articleDetail(Long articleId) {
        ArticleResponseDto articleResponseDto = queryFactory
                .select(
                        new QArticleResponseDto(
                                user.username.as("username"),
                                article.id.as("articleId"),
                                article.title.as("articleTitle"),
                                article.content.as("articleContent"),
                                article.viewCount.as("viewCount"),
                                article.createdAt.as("createdDateTime"),
                                articleComment.id.count().as("totalArticleComment")
                        )
                )
                .from(article)
                .leftJoin(article.articleCommentList, articleComment)
                .leftJoin(article.user, user)
                .where(article.id.eq(articleId))
                .fetchOne();

        List<ArticleCommentResponseDto> articleCommentResponseDtoList = queryFactory
                .select(
                        new QArticleCommentResponseDto(
                                user.username.as("username"),
                                articleComment.id.as("articleCommentId"),
                                articleComment.comment.as("comment"),
                                articleComment.createdAt.as("createdDateTime")
                        )
                )
                .from(articleComment)
                .leftJoin(articleComment.user, user)
                .where(articleComment.article.id.eq(articleId))
                .orderBy(articleComment.createdAt.desc())
                .fetch();

        articleResponseDto.setArticleComments(articleCommentResponseDtoList);

        return articleResponseDto;
    }

}
