package com.myboard.dto.requestDto.search;

import com.myboard.exception.search.InvalidSearchTypeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
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
            return BOARD;
        }

        return Arrays.stream(values())
                .filter(searchType -> searchType.value.equals(value))
                .findFirst()
                .orElseThrow(InvalidSearchTypeException::new);
    }
}
