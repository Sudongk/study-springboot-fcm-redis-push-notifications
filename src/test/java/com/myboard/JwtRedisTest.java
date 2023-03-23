package com.myboard;

import com.myboard.jwt.JwtTokenManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JwtRedisTest {

    @Autowired
    private JwtTokenManager jwtTokenManager;

    private static final String USER_NAME = "USERNAME";
    private static final String TOKEN = "qwerasdzxc";

    @AfterEach
    void clear() {
        jwtTokenManager.deleteToken(USER_NAME);
    }

    @DisplayName("fcm 토큰 CRUD 테스트")
    @Nested
    class jwtCRUD {

        @DisplayName("jwt 토큰 저장")
        @Test
        void save() {
            jwtTokenManager.saveToken(USER_NAME, TOKEN);

            String token = jwtTokenManager.getToken(USER_NAME);

            assertThat(token).isNotNull();
        }


        @DisplayName("jwt 토큰 조회")
        @Test
        void read() {
            jwtTokenManager.saveToken(USER_NAME, TOKEN);

            String token = jwtTokenManager.getToken(USER_NAME);

            assertThat(token).isNotNull();
            assertThat(token).isEqualTo(TOKEN);
        }

        @DisplayName("없는 jwt 토큰 조회시 null 반환")
        @Test
        void readWhenNotExist() {
            String token = jwtTokenManager.getToken("NOTEXIST");

            assertThat(token).isNull();
            assertThat(token).isEqualTo(null);
        }

        @DisplayName("jwt 토큰 삭제")
        @Test
        void delete() {
            jwtTokenManager.saveToken(USER_NAME, TOKEN);

            jwtTokenManager.deleteToken(USER_NAME);

            String token = jwtTokenManager.getToken(USER_NAME);

            assertThat(token).isNull();
        }
    }
}
