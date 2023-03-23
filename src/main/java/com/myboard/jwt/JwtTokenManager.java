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
    private RedisTemplate<String, String> redisTemplate;

    private void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    private String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    private void del(String key) {
        redisTemplate.delete(key);
    }

    private Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Async
    public void saveToken(String username, String token) {
        log.info("JwtTokenManager saveToken");
        set(username, token);
    }

    public String getToken(String username) {
        log.info("JwtTokenManager getToken");
        return get(username);
    }

    @Async
    public void deleteToken(String username) {
        log.info("JwtTokenManager deleteToken");
        del(username);
    }

    public Boolean isExistToken(String username) {
        return exists(username);
    }

    @Async
    public void deleteAndSaveToken(String username, String token) {
        log.info("JwtTokenManager deleteAndSaveToken");
        del(username);
        set(username, token);
    }
}
