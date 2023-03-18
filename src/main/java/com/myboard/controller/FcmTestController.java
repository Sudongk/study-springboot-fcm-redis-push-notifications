package com.myboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FcmTestController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/fcm")
    public String fcmTest(){
        return "fcm_test";
    }
}