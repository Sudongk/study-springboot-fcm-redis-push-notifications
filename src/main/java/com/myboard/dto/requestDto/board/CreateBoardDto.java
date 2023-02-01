package com.myboard.dto.requestDto.board;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(of = "boardName")
@NoArgsConstructor
public class CreateBoardDto {

    @NotBlank(message = "B002")
    @Length(min = 1, max = 30, message = "B001")
    private String boardName;

    List<String> tagNames = new ArrayList<>();

    @Builder
    public CreateBoardDto(String boardName, List<String> tagNames) {
        this.boardName = boardName;
        this.tagNames = tagNames;
    }
}
