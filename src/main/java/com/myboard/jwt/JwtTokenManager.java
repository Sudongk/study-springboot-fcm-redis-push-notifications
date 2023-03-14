package com.myboard.jwt;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Log4j2
@Component
public class JwtTokenManager {

    @Value("${firebase.firebaseConfigPath}")
    private String firebaseConfigPath;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    public void saveToken(String username, String token) {
        log.info("JwtTokenManager saveToken");
        redisTemplate.opsForValue().set(username, token);
    }

    public String getToken(String username) {
        log.info("JwtTokenManager getToken");
        return (String) redisTemplate.opsForValue().get(username);
    }

    public void removeToken(String username) {
        log.info("JwtTokenManager removeToken");
        redisTemplate.delete(username);
    }

    public Boolean isExistToken(String username) {
        return redisTemplate.hasKey(username);
    }
}
