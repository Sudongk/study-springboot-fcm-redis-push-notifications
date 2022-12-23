package com.myboard.controller.articleComment;

import com.myboard.aop.resolver.CurrentLoginUserId;
import com.myboard.aop.valid.CheckExist;
import com.myboard.aop.valid.EntityType;
import com.myboard.dto.requestDto.articleComment.CreateArticleCommentDto;
import com.myboard.dto.requestDto.articleComment.UpdateArticleCommentDto;
import com.myboard.service.articleComment.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/articleComment/create/{articleId}")
    public ResponseEntity<Long> createArticleComment(@Valid @RequestBody CreateArticleCommentDto createArticleCommentDto,
                                                     @CheckExist(type = EntityType.ARTICLE, message = "A005") @PathVariable("articleId") Long articleId,
                                                     @CurrentLoginUserId Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleCommentService.createArticleComment(createArticleCommentDto, articleId, userId));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/articleComment/update/{articleCommentId}")
    public ResponseEntity<Long> updateArticleComment(@Valid @RequestBody UpdateArticleCommentDto updateArticleCommentDto,
                                                     @CheckExist(type = EntityType.COMMENT, message = "C003") @PathVariable("articleCommentId") Long articleCommentId,
                                                     @CurrentLoginUserId Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleCommentService.updateArticleComment(updateArticleCommentDto, articleCommentId, userId));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/articleComment/delete/{articleCommentId}")
    public ResponseEntity<Long> deleteArticleComment(@CheckExist(type = EntityType.COMMENT, message = "C003") @PathVariable("articleCommentId") Long articleCommentId,
                                                     @CurrentLoginUserId Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleCommentService.deleteArticleComment(articleCommentId, userId));
    }
}
