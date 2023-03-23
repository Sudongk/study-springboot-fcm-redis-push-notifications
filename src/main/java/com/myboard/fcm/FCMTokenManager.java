package com.myboard.fcm;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Log4j2
@Component
public class FCMTokenManager {

    @Value("${firebase.firebaseConfigPath}")
    private String firebaseConfigPath;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /*
        RedisTemplate 사용 타입
        opsForValue()	ValueOperations	Strings를 쉽게 Serialize / Deserialize 해주는 Interface
        opsForList()	ListOperations	List를 쉽게 Serialize / Deserialize 해주는 Interface
        opsForSet()	    SetOperations Set를 쉽게 Serialize / Deserialize 해주는 Interface
        opsForZSet()	ZSetOperations	ZSet를 쉽게 Serialize / Deserialize 해주는 Interface
        opsForHash()	HashOperations	Hash를 쉽게 Serialize / Deserialize 해주는 Interface
    */

    // 레디스에 토근 저장
    private void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // userId를 이용해 사용자 토근 조회
    private String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // userId를 이용해 사용자 토근 삭제
    private void del(String key) {
        redisTemplate.delete(key);
    }

    @Async
    public void saveToken(String userId, String token) {
        set(userId, token);
    }

    public String getToken(String userId) {
        return get(userId);
    }

    @Async
    public void deleteToken(String userId) {
        del(userId);
    }

    @Async
    public void deleteAndSaveFCMToken(String userId, String token) {
        del(userId);
        set(userId, token);
    }
}
