package com.myboard.controller.search;

import com.myboard.aop.resolver.SearchParams;
import com.myboard.dto.requestDto.search.SearchParameter;
import com.myboard.dto.responseDto.search.SearchResponseDto;
import com.myboard.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<SearchResponseDto<?>> search(@SearchParams SearchParameter searchParameter) {

        SearchResponseDto<?> search = searchService.search(searchParameter);

        return ResponseEntity.status(HttpStatus.OK)
                .body(search);
    }

}
