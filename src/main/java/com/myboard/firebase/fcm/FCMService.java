package com.myboard.firebase.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    public void sendMessage(String token, String title, String contents) {
        log.info("FCMService sendMessage");
        // setToken 혹은 setTopic을 이용해 메세지의 타겟을 결정
        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(
                        WebpushConfig.builder()
                                .putHeader("HeaderKey", "HeaderValue")
                                .setNotification(new WebpushNotification(title, contents))
                                .build()
                )
                .build();
        try {
            String messageResponse = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("Sent Message: {}", messageResponse);
        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException("알림 전송에 실패하였습니다.");
        }
    }
}
