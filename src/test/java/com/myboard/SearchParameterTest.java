package com.myboard;

import com.myboard.dto.requestDto.search.SearchParameter;

import com.myboard.exception.search.InvalidPageSizeException;
import com.myboard.exception.search.InvalidPageStartException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class SearchParameterTest {


    @Test
    @DisplayName("검색 요청 SearchParameter 인스턴스 생성 성공")
    void createSearchParameterSuccessful() {
        // when, then
        assertThatCode(() -> SearchParameter.of(
                        "keyword",
                        "board",
                        "0",
                        "20"
                )
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("검색 요청 SearchParameter 인스턴스 생성 실패 - start 값이 음수인 경우")
    void createSearchParameterWithInValidStart() {
        // when, then
        assertThatThrownBy(() -> SearchParameter.of(
                        "keyword",
                        "board",
                        "-1",
                        "20"
                )
        ).isInstanceOf(InvalidPageStartException.class);
    }

    @Test
    @DisplayName("검색 요청 SearchParameter 인스턴스 생성 실패 - 유효하지 않은 size 값인 경우")
    void createSearchParameterWithInValidSize() {
        // when, then
        assertThatThrownBy(() -> SearchParameter.of(
                        "keyword",
                        "board",
                        "0",
                        "0"
                )
        ).isInstanceOf(InvalidPageSizeException.class);
    }
}
