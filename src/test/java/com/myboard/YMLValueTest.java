package com.myboard;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.*;

@TestPropertySource(locations = "classpath:application.yml")
@SpringBootTest
public class YMLValueTest {

    private static final String CONFIGPATH = "/firebase/board-api-e5a2c-firebase-adminsdk-fubgi-e15e1f43ed.json";
    private static final String SCOPE = "https://www.googleapis.com/auth/cloud-platform";
    private static final String TITLE = "Hello";
    private static final String MESSAGE = "SendingTestMessage";
    private static final String TOKEN = "c58FYJjHRuaUL42adIg8Sn:APA91bHGInsolgB4mKiJ8hmYB1CWCTBZzFj3tOWvF2XKA1r4fusm9L1xTni1NH13B4HtPMvCtkp0544mIgr6ukL4svZpQ65FtaIfvkk_GzGpAIe0ICbR3oBW9KnpbMbqAbNU2xfUpb5G";

    @Value("${firebase.firebaseConfigPath}")
    private String firebaseConfigPath;

    @Value("${firebase.scope}")
    private String scope;

    @Value("${firebase.notifications.defaults.title}")
    private String title;

    @Value("${firebase.notifications.defaults.message}")
    private String message;

    @Value("${firebase.notifications.defaults.token}")
    private String token;

    @Test
    @DisplayName("프로퍼티 불러오기 테스트")
    void valueTest() {
        assertThat(firebaseConfigPath).isEqualTo(CONFIGPATH);
        assertThat(scope).isEqualTo(SCOPE);
        assertThat(title).isEqualTo(TITLE);
        assertThat(message).isEqualTo(MESSAGE);
        assertThat(token).isEqualTo(TOKEN);
    }

}
