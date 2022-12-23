package com.myboard.dto.requestDto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateBoardDto {

    @NotBlank(message = "B002")
    @Length(min = 1, max = 30, message = "B001")
    private String boardName;

    List<String> tagNames = new ArrayList<>();

}
