package com.myboard.controller.user;

import com.myboard.aop.resolver.LoginUserId;
import com.myboard.aop.valid.CheckExist;
import com.myboard.aop.valid.EntityType;
import com.myboard.dto.requestDto.user.UserCreateRequestDto;
import com.myboard.service.user.UserService;
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
@RequestMapping(("/api/v1"))
@Validated
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/user/create")
    public ResponseEntity<Long> userCreate(@Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.userCreate(userCreateRequestDto));
    }

    /**
     * 회원탈퇴
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/user/delete/{userId}")
    public ResponseEntity<Long> userDelete(@CheckExist(type = EntityType.USER, message = "U001") @PathVariable Long userId,
                                           @LoginUserId Long currentUserId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.userDelete(userId, currentUserId));
    }


}

