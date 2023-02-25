package com.myboard.dto.requestDto.articleComment;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@EqualsAndHashCode(of = "comment")
@NoArgsConstructor
public class UpdateArticleCommentDto {

    @NotBlank(message = "C002")
    @Length(min = 1, max = 200, message = "C001")
    private String comment;

    @Builder
    public UpdateArticleCommentDto(String comment) {
        this.comment = comment;
    }
}
