package com.myboard.dto.responseDto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {

    private Long userId;
    private String username;

    @Builder
    public UserResponseDto(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
