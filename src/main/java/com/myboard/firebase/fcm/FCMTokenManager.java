package com.myboard.firebase.fcm;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
