package com.myboard.dto.requestDto.search;

import com.myboard.exception.search.InvalidPageSizeException;
import com.myboard.exception.search.InvalidPageStartException;
import lombok.*;
import org.springframework.data.domain.PageRequest;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchParameter {

    private static final int MIN_START_PAGE = 0;
    private static final int MIN_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private static final double id = Math.random();
    private SearchKeyword searchKeyword;
    private SearchType searchType;
    private int start;
    private int size;

    public static SearchParameter of(String searchKeyword, String searchType, String start, String size) {
        return new SearchParameter(
                SearchKeyword.of(searchKeyword),
                SearchType.of(searchType),
                initializeStart(start),
                initializeSize(size)
        );
    }

    public PageRequest ofPageRequest() {
        return PageRequest.of(start, size);
    }

    private static int initializeStart(String start) {
        try {
            int value = Integer.parseInt(start);
            if (value < MIN_START_PAGE) {
                throw new InvalidPageStartException();
            }
            return value;
        } catch (NumberFormatException e) {
            return MIN_START_PAGE;
        }
    }

    private static int initializeSize(String size) {
        try {
            int value = Integer.parseInt(size);
            if (value < MIN_PAGE_SIZE || value > MAX_PAGE_SIZE) {
                throw new InvalidPageSizeException();
            }
            return value;
        } catch (NumberFormatException e) {
            return MIN_PAGE_SIZE;
        }
    }

}
