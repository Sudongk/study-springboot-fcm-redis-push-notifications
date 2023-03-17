package com.myboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FcmTestController {

    @GetMapping("/api/v1/fcm")
    public String fcmTest(){
        return "test";
    }

}