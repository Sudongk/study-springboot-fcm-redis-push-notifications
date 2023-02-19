package com.myboard.dto.requestDto.search;

import com.myboard.exception.search.LongSearchKeywordException;
import com.myboard.exception.search.SearchKeywordNullException;
import com.myboard.exception.search.ShortSearchKeywordException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode(of = "value")
public class SearchKeyword {

    private static final int KEYWORD_MIN_LENGTH = 1;
    private static final int KEYWORD_MAX_LENGTH = 30;

    private String value;

    private SearchKeyword(String value) {
        this.value = value;
    }

    public static SearchKeyword of(String value) {
        validateNonNull(value);
        String replaceValue = replaceValue(value);
        validateLength(replaceValue);

        return new SearchKeyword(replaceValue);
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

    private static String replaceValue(String value) {
        return value.replaceAll("(\\t|\r\n|\r|\n|\n\r)", " ");
    }
}
