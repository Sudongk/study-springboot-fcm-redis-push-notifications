package com.myboard.service.alert;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.myboard.firebase.fcm.FcmTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final FcmTokenManager FCMTokenManager;

    // 토근값으로 특정 사용자에게 알림 전송
    public void sendMessage(String token, String title, String contents) {
        // setToken 혹은 setTopic을 이용해 메세지의 타겟을 정한다.
        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(WebpushConfig.builder().putHeader("HeaderKey", "HeaderValue")
                        .setNotification(new WebpushNotification(title, contents))
                        .build()).build();

        try {
            FirebaseMessaging.getInstance().sendAsync(message).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException("알림 전송에 실패하였습니다.");
        }
    }
}
