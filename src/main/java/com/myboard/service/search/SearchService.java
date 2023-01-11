package com.myboard.service.search;

import com.myboard.dto.requestDto.search.SearchParameter;
import com.myboard.dto.responseDto.search.SearchResponseDto;


public interface SearchService {

    SearchResponseDto search(SearchParameter searchParameter);

}
