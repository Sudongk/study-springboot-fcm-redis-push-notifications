package com.myboard.firebase.fcm;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Log4j2
@Component
public class FcmTokenManager {

    @Value("${firebase.firebaseConfigPath}")
    private String firebaseConfigPath;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // 레디스에 토근 저장
    // 토큰을 userId와 함께 세션과 함께 저장하기에는 토큰의 주기가 다르기때문에 별도로 저장
    public void saveToken(String userId, String token) {
        redisTemplate.opsForValue().set(userId, token);
    }

    // userId를 이용해 사용자 토근 조회
    public String getToken(String userId) {
        return redisTemplate.opsForValue().get(userId);
    }

    // userId를 이용해 사용자 토근 삭제
    public void removeToken(String userId) {
        redisTemplate.delete(userId);
    }
}
