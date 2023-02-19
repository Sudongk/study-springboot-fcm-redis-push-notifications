package com.myboard;

import com.myboard.dto.requestDto.search.SearchKeyword;
import com.myboard.exception.search.LongSearchKeywordException;
import com.myboard.exception.search.SearchKeywordNullException;
import com.myboard.exception.search.ShortSearchKeywordException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;

public class SearchKeywordTest {

    @Test
    @DisplayName("SearchKeyword 생성 성공 - keyword값이 같으면 같은 객체")
    void createSearchKeywordEqualValue() {
        // given
        String keyword = "keyword";

        // when
        SearchKeyword searchKeyword = SearchKeyword.of(keyword);

        // then
        assertThat(searchKeyword).isEqualTo(
                SearchKeyword.of(keyword)
        );
    }

    @Test
    @DisplayName("SearchKeyword 생성 성공 - keyword값이 다르면 다른 객체")
    void createSearchKeywordNotEqualValue() {
        // given
        String keyword = "keyword";
        String diffKeyword = "diffKeyword";

        // when
        SearchKeyword searchKeyword = SearchKeyword.of(keyword);

        // then
        assertThat(searchKeyword).isNotEqualTo(
                SearchKeyword.of(diffKeyword)
        );
    }

    @Test
    @DisplayName("SearchKeyword 생성시 개행문자 공백으로 변경")
    void replaceAllValueWhenCreateSearchKeyword() {
        // given
        SearchKeyword afterReplace = SearchKeyword.of("k e  y w o r   d  ");

        SearchKeyword beforeReplace = SearchKeyword.of("k\te\n\ny\tw\to\nr\n\n\nd\n\t");

        // when, then
        assertThat(beforeReplace).isEqualTo(afterReplace);
    }

    @Test
    @DisplayName("value 값이 null이면 예외 발생")
    void throwExceptionWhenCreateWithValueIsNull() {
        // given
        String keyword = null;

        // when, then
        assertThatThrownBy(() -> SearchKeyword.of(keyword))
                .isInstanceOf(SearchKeywordNullException.class);
    }

    @Test
    @DisplayName("최소 길이보다 짧은 문자열 검색일 경우 예외 발생")
    void throwExceptionWhenCreateWithShortString() {
        // given
        String keyword = "";

        // when, then
        assertThatThrownBy(() -> SearchKeyword.of(keyword))
                .isInstanceOf(ShortSearchKeywordException.class);
    }

    @Test
    @DisplayName("최대 길이보다 짧은 문자열 검색일 경우 예외 발생")
    void throwExceptionWhenCreateWithLongString() {
        // given
        String keyword = RandomStringUtils.randomAlphabetic(100);

        // when, then
        assertThatThrownBy(() -> SearchKeyword.of(keyword))
                .isInstanceOf(LongSearchKeywordException.class);
    }

    private static String randomStringGenerator() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int stringLength = 100;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(stringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
