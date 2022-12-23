package com.myboard.service.user;

import com.myboard.dto.requestDto.user.UserCreateRequestDto;
import com.myboard.dto.responseDto.user.UserResponseDto;

public interface UserService {
    Long userCreate(UserCreateRequestDto userCreateRequestDto);

    Long userDelete(Long userId, Long currentUserId);

}
