package com.myboard.dto.requestDto.search;

import com.myboard.exception.search.LongSearchKeywordException;
import com.myboard.exception.search.SearchKeywordNullException;
import com.myboard.exception.search.ShortSearchKeywordException;
import lombok.Getter;

import java.util.Objects;

@Getter
public class SearchKeyword {

    private static final int KEYWORD_MIN_LENGTH = 1;
    private static final int KEYWORD_MAX_LENGTH = 30;

    private String value;

    private SearchKeyword(String value) {
        this.value = value;
    }

    public static SearchKeyword of(String value) {
        String newString = value.toLowerCase();
        validateNonNull(newString);
        validateLength(newString);

        return new SearchKeyword(newString);
    }

    private static void validateNonNull(String value) {
        if (Objects.isNull(value)) {
            throw new SearchKeywordNullException();
        }
    }

    private static void validateLength(String value) {
        if (value.length() > KEYWORD_MAX_LENGTH) {
            throw new LongSearchKeywordException();
        }
        if (value.length() < KEYWORD_MIN_LENGTH) {
            throw new ShortSearchKeywordException();
        }
    }

}
