package com.myboard.controller;

import com.myboard.aop.resolver.CurrentLoginUserId;
import com.myboard.aop.resolver.SearchParams;
import com.myboard.aop.valid.CheckExist;
import com.myboard.aop.valid.EntityType;
import com.myboard.dto.requestDto.search.SearchParameter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@Validated
public class CustomAnnotationTestController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/checkExistAnnotationTest/user/{userId}")
    public Long checkExistAnnotationUserTest(@CheckExist(type = EntityType.USER, message = "B003") @PathVariable Long userId) {
        return userId;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/checkExistAnnotationTest/board/{boardId}")
    public Long checkExistAnnotationBoardTest(@CheckExist(type = EntityType.BOARD, message = "B003") @PathVariable Long boardId) {
        return boardId;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/checkExistAnnotationTest/article/{articleId}")
    public Long checkExistAnnotationArticleTest(@CheckExist(type = EntityType.ARTICLE, message = "B003") @PathVariable Long articleId) {
        return articleId;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/checkExistAnnotationTest/comment/{commentId}")
    public Long checkExistAnnotationCommentTest(@CheckExist(type = EntityType.COMMENT, message = "B003") @PathVariable Long commentId) {
        return commentId;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/currentLoginUserIdAnnotationTest")
    public Long currentLoginUserIdAnnotation(@CurrentLoginUserId Long userId) {
        return userId;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/searchParamsAnnotationTest")
    public Map<String, Object> SearchParamsAnnotation(@SearchParams SearchParameter searchParameter) {
        return SearchParameter.toMap(searchParameter);
    }

}
