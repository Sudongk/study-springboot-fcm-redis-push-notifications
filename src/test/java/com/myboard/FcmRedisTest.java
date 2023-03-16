package com.myboard;

import com.myboard.firebase.fcm.FcmTokenManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class FcmRedisTest {

    @Autowired
    private FcmTokenManager fcmTokenManager;

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "USERNAME";
    private static final String TOKEN = "qwerasdzxc";

    @AfterEach
    void clear() {
        fcmTokenManager.removeToken(String.valueOf(USER_ID));
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

        @DisplayName("fcm 토큰 삭제")
        @Test
        void delete() {
            fcmTokenManager.saveToken(String.valueOf(USER_ID), TOKEN);

            fcmTokenManager.removeToken(String.valueOf(USER_ID));

            String token = fcmTokenManager.getToken(String.valueOf(USER_ID));

            assertThat(token).isNull();
        }
    }
}