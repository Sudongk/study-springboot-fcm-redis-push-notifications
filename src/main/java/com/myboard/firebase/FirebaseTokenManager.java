package com.myboard.firebase;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Log4j2
@Component
public class FirebaseTokenManager {

    @Value("${firebase.firebaseConfigPath}")
    private String firebaseConfigPath;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    // 레디스에 토근 저장
    // 토큰을 userId와 함께 세션과 함께 저장하기에는 토큰의 주기가 다르기때문에 별도로 저장
    public void saveToken(Long userId, String token) {
        redisTemplate.opsForValue().set(userId, token);
    }

    // userId를 이용해 사용자 토근 조회
    public String getToken(Long userId) {
        return (String) redisTemplate.opsForValue().get(userId);
    }

    // userId를 이용해 사용자 토근 삭제
    public void removeToken(Long userId) {
        redisTemplate.delete(userId);
    }
}
