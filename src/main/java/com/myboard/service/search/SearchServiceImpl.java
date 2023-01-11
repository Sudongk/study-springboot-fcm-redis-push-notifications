package com.myboard.service.search;

import com.myboard.dto.requestDto.search.SearchParameter;
import com.myboard.dto.requestDto.search.SearchType;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.dto.responseDto.search.SearchResponseDto;
import com.myboard.repository.search.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchRepository searchRepository;

    @Override
    public SearchResponseDto search(SearchParameter searchParameter) {

        String type = searchParameter.getSearchType().getValue();
        String articleType = SearchType.ARTICLE.getValue();
        String boardType = SearchType.BOARD.getValue();
        PageRequest pageRequest = searchParameter.ofPageRequest();

        SearchResponseDto result = new SearchResponseDto();

        if (type.equals(articleType)) {
            Page<ArticleResponseDto> searchResult = searchRepository.searchArticle(searchParameter, pageRequest);
            result.setResult(searchResult);
        }

        if (type.equals(boardType)) {
            Page<BoardResponseDto> searchResult = searchRepository.searchBoard(searchParameter, pageRequest);
            result.setResult(searchResult);
        }

        return result;
    }
}
