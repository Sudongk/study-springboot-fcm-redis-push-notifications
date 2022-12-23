package com.myboard.service.article;

import com.myboard.dto.requestDto.article.CreateArticleDto;
import com.myboard.dto.requestDto.article.UpdateArticleDto;
import com.myboard.dto.responseDto.article.ArticleResponseDto;


public interface ArticleService {
    Long createArticle(CreateArticleDto createArticleDto, Long boardId, Long userId);

    Long updateArticle(UpdateArticleDto updateArticleDto, Long articleId, Long userId);

    Long deleteArticle(Long articleId, Long userId);

    ArticleResponseDto articleDetail(Long articleId);
}
