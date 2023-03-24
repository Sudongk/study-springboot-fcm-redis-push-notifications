package com.myboard.dto.requestDto.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@EqualsAndHashCode(of = "username")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginRequestDto {

    @NotBlank(message = "U003")
    @Length(min = 1 ,max = 20, message = "U004")
    private String username;

    @NotBlank(message = "U007")
    @Length(min = 8, max = 30, message = "U008")
    private String password;

    private String fcmToken;

    // 테스트용
//    @Builder
//    public UserLoginRequestDto(String username, String password) {
//        this.username = username;
//        this.password = password;
//    }

    @Builder
    public UserLoginRequestDto(String username, String password, String fcmToken) {
        this.username = username;
        this.password = password;
        this.fcmToken = fcmToken;
    }
}
