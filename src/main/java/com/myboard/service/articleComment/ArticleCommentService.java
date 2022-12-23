package com.myboard.service.articleComment;

import com.myboard.dto.requestDto.articleComment.CreateArticleCommentDto;
import com.myboard.dto.requestDto.articleComment.UpdateArticleCommentDto;

public interface ArticleCommentService {

    Long createArticleComment(CreateArticleCommentDto createArticleCommentDto, Long articleId, Long userId);

    Long updateArticleComment(UpdateArticleCommentDto updateArticleCommentDto, Long articleCommentId, Long userId);

    Long deleteArticleComment(Long articleCommentId, Long userId);
}
