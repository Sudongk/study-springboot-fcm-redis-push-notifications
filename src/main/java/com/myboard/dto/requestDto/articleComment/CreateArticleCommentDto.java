package com.myboard.dto.requestDto.articleComment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class CreateArticleCommentDto {

    @NotBlank(message = "C002")
    @Length(min = 1, max = 200, message = "C001")
    private String comment;

    @Builder
    public CreateArticleCommentDto(String comment) {
        this.comment = comment;
    }
}
