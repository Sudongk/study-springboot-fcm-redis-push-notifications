package com.myboard.controller;

import com.myboard.aop.resolver.CurrentLoginUserId;
import com.myboard.aop.valid.CheckExist;
import com.myboard.aop.valid.EntityType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Validated
public class TestController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test1/{id}")
    public Long test(@CheckExist(type = EntityType.BOARD, message = "B003") @PathVariable Long id) {
        return id;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test2")
    public Object test2(Authentication authentication) {
        return authentication.getPrincipal();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/test3")
    public Long test3(@CurrentLoginUserId Long userId) {
        return userId;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/test4/{articleId}")
    public Long test4(@CurrentLoginUserId Long userId,
                      @CheckExist(type = EntityType.ARTICLE, message = "A005") @PathVariable("articleId") Long articleId) {
        return articleId;
    }

}
