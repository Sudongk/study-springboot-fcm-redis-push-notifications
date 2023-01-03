package com.myboard.dto.requestDto.search;

import com.myboard.exception.search.InvalidSearchTypeException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum SearchType {

    ARTICLE("article"),
    BOARD("board");

    private String value;

    SearchType(String value) {
        this.value = value;
    }

    public static SearchType of(String value) {
        if (Objects.isNull(value)) {
            return ARTICLE;
        }

        return Arrays.stream(values())
                .filter(searchType -> searchType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(InvalidSearchTypeException::new);
    }

}
