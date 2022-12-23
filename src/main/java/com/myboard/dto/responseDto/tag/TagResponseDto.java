package com.myboard.dto.responseDto.tag;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TagResponseDto {

    private Long boardId;
    private List<String> tagName;

    @Builder
    @QueryProjection
    public TagResponseDto(Long boardId, List<String> tagName) {
        this.boardId = boardId;
        this.tagName = tagName;
    }
}
