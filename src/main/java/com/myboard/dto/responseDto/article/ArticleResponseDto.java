package com.myboard.dto.responseDto.article;

import com.myboard.dto.responseDto.articleComment.ArticleCommentResponseDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleResponseDto {

    private String username;
    private Long articleId;
    private String articleTitle;
    private String articleContent;
    private Long viewCount;
    private LocalDateTime createdDateTime;
    private Long totalArticleComment;
    List<ArticleCommentResponseDto> articleComments = new ArrayList<>();


    // board 상세 조회용
    @Builder
    @QueryProjection
    public ArticleResponseDto(String username, Long articleId, String articleTitle, Long viewCount, LocalDateTime createdDateTime) {
        this.username = username;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleContent = null;
        this.viewCount = viewCount;
        this.createdDateTime = createdDateTime;
        this.totalArticleComment = null;
        this.articleComments = null;
    }

    // article 상세 조회용
    @Builder
    @QueryProjection
    public ArticleResponseDto(String username, Long articleId, String articleTitle, String articleContent, Long viewCount, LocalDateTime createdDateTime, Long totalArticleComment) {
        this.username = username;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleContent = articleContent;
        this.viewCount = viewCount;
        this.createdDateTime = createdDateTime;
        this.totalArticleComment = totalArticleComment;
    }

    public void setArticleComments(List<ArticleCommentResponseDto> articleComments) {
        if (!CollectionUtils.isEmpty(articleComments)) {
            this.articleComments = articleComments;
        }
    }
}
