package com.myboard;

import com.myboard.dto.requestDto.search.SearchType;
import com.myboard.exception.search.InvalidSearchTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

public class SearchTypeTest {

    @ParameterizedTest
    @EnumSource(SearchType.class)
    @DisplayName("SearchType 생성 성공")
    void createSearchType(SearchType value) {
        // when
        SearchType searchType = SearchType.of(value.getValue());

        // then
        assertThat(searchType.getValue()).isEqualTo(value.getValue());
    }

    @Test
    @DisplayName("SearchType 값이 null 일 경우 디폴트 값 BOARD 로 생성")
    void createDefaultValueWhenValueIsNull() {
        // when
        SearchType searchType = SearchType.of(null);

        // then
        assertThat(searchType).isEqualTo(SearchType.BOARD);
    }

    @Test
    @DisplayName("유효하지 않은 값으로 SearchType 생성시 예외 발생")
    void throwExceptionWhenCreateWithInValidValue() {
        // given
        String value = "comment";

        // when, then
        assertThatThrownBy(() -> SearchType.of(value))
                .isInstanceOf(InvalidSearchTypeException.class);
    }
}
