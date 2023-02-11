package com.myboard.controller.article;

import com.myboard.aop.resolver.CurrentLoginUserId;
import com.myboard.aop.valid.CheckExist;
import com.myboard.aop.valid.EntityType;
import com.myboard.dto.requestDto.article.CreateArticleDto;
import com.myboard.dto.requestDto.article.UpdateArticleDto;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.service.article.ArticleService;
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
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 아티클 생성
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/article/create/{boardId}")
    public ResponseEntity<Long> createArticle(@Valid @RequestBody CreateArticleDto createArticleDto,
                                              @CheckExist(type = EntityType.BOARD, message = "B003") @PathVariable("boardId") Long boardId,
                                              @CurrentLoginUserId Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleService.createArticle(createArticleDto, boardId, userId));
    }

    /**
     * 아티클 수정
     */
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/article/{articleId}/update")
    public ResponseEntity<Long> updateArticle(@Valid @RequestBody UpdateArticleDto updateArticleDto,
                                              @CheckExist(type = EntityType.ARTICLE, message = "A005") @PathVariable("articleId") Long articleId,
                                              @CurrentLoginUserId Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.updateArticle(updateArticleDto, articleId, userId));
    }

    /**
     * 게시글 삭제
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/article/{articleId}/delete")
    public ResponseEntity<Long> deleteArticle(@CheckExist(type = EntityType.ARTICLE, message = "A005") @PathVariable("articleId") Long articleId,
                                              @CurrentLoginUserId Long userId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.deleteArticle(articleId, userId));
    }

    /**
     * 게시글 상세 조회
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/article/{articleId}")
    public ResponseEntity<ArticleResponseDto> articleDetail(@CheckExist(type = EntityType.ARTICLE, message = "A005") @PathVariable("articleId") Long articleId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.articleDetail(articleId));
    }
}
