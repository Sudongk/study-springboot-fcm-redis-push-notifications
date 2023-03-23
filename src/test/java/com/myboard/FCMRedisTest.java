package com.myboard;

import com.myboard.fcm.FCMTokenManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class FCMRedisTest {

    @Autowired
    private FCMTokenManager fcmTokenManager;

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "USERNAME";
    private static final String TOKEN = "qwerasdzxc";

    @AfterEach
    void clear() {
        fcmTokenManager.deleteToken(String.valueOf(USER_ID));
    }

    @DisplayName("fcm 토큰 CRUD 테스트")
    @Nested
    class fcmCRUD {

        @DisplayName("fcm 토큰 저장")
        @Test
        void save() {
            fcmTokenManager.saveToken(String.valueOf(USER_ID), TOKEN);

            String token = fcmTokenManager.getToken(String.valueOf(USER_ID));

            assertThat(token).isNotNull();
        }

        @DisplayName("fcm 토큰 조회")
        @Test
        void read() {
            fcmTokenManager.saveToken(String.valueOf(USER_ID), TOKEN);

            String token = fcmTokenManager.getToken(String.valueOf(USER_ID));

            assertThat(token).isNotNull();
            assertThat(token).isEqualTo(TOKEN);
        }

        @DisplayName("없는 fcm 토큰 조회시 null 반환")
        @Test
        void readWhenNotExist() {
            String token = fcmTokenManager.getToken("NOTEXIST");

            assertThat(token).isNull();
            assertThat(token).isEqualTo(null);
        }

        @DisplayName("fcm 토큰 삭제")
        @Test
        void delete() {
            fcmTokenManager.saveToken(String.valueOf(USER_ID), TOKEN);

            fcmTokenManager.deleteToken(String.valueOf(USER_ID));

            String token = fcmTokenManager.getToken(String.valueOf(USER_ID));

            assertThat(token).isNull();
        }
    }
}
