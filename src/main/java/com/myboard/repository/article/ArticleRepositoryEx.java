package com.myboard.repository.article;


import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.articleComment.ArticleCommentResponseDto;

import java.util.List;


public interface ArticleRepositoryEx {
    ArticleResponseDto articleDetail(Long articleId);
}
