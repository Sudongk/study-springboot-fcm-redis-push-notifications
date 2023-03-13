package com.myboard.dto.responseDto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {

    private Long userId;
    private String username;
    private String token;

    @Builder
    public UserResponseDto(Long userId, String username, String token) {
        this.userId = userId;
        this.username = username;
        this.token = token;
    }
}
