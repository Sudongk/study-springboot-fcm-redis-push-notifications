package com.myboard.dto.requestDto.article;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateArticleDto {

    @NotBlank(message = "A002")
    @Length(min = 1, max = 30, message = "A001")
    private String articleTitle;

    @NotBlank(message = "A003")
    @Length(max = 2000, message = "A004")
    private String articleContent;

    @Builder
    public UpdateArticleDto(String articleTitle, String articleContent, List<String> attachmentLocations) {
        this.articleTitle = articleTitle;
        this.articleContent = articleContent;
    }

}
