package com.myboard.dto.responseDto.articleComment;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ArticleCommentResponseDto {

    private String username;
    private Long articleCommentId;
    private String comment;
    private LocalDateTime createdDateTime;

    @Builder
    @QueryProjection
    public ArticleCommentResponseDto(String username, Long articleCommentId, String comment, LocalDateTime createdDateTime) {
        this.username = username;
        this.articleCommentId = articleCommentId;
        this.comment = comment;
        this.createdDateTime = createdDateTime;
    }
}
