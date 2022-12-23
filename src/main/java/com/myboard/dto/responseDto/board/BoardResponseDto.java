package com.myboard.dto.responseDto.board;

import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.tag.TagResponseDto;
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
public class BoardResponseDto {

    private Long boardId;
    private String boardName;
    private LocalDateTime createdDateTime;
    private Long totalArticle;
    private Long totalNewArticle;
    private List<TagResponseDto> tags = new ArrayList<>();
    private List<ArticleResponseDto> articleResponseDtoList = new ArrayList<>();

    // board 리스트 조회용
    @Builder
    @QueryProjection
    public BoardResponseDto(Long boardId, String boardName, LocalDateTime createdDateTime, Long totalArticle, Long totalNewArticle) {
        this.boardId = boardId;
        this.boardName = boardName;
        this.createdDateTime = createdDateTime;
        this.totalArticle = totalArticle;
        this.totalNewArticle = totalNewArticle;
        this.articleResponseDtoList = null;
    }

    // board 상세 조회용
    @Builder
    @QueryProjection
    public BoardResponseDto(Long boardId, String boardName, LocalDateTime createdDateTime) {
        this.boardId = boardId;
        this.boardName = boardName;
        this.createdDateTime = createdDateTime;
        this.totalArticle = null;
        this.totalNewArticle = null;
    }

    public void setTags(List<TagResponseDto> tags) {
        this.tags = tags;
    }

    public void setArticleResponseDtoList(List<ArticleResponseDto> articleResponseDtoList) {
        if (!CollectionUtils.isEmpty(articleResponseDtoList)) {
            this.articleResponseDtoList = articleResponseDtoList;
        }
    }
}
