package com.sky.interceptor.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Data
public class JwtRedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final String JWT_KEY_PREFIX = "jwt:";
    private static final String USER_SET_KEY_PREFIX = "user:";
    /**
     * 将jwt令牌存储到redis中
     * @param userId
     * @param jwt
     */
    public void saveJwtToRedis(String userId, String jwt, long timeout) {
        String jwtKey = JWT_KEY_PREFIX + jwt;
        String userSetKey = USER_SET_KEY_PREFIX + userId;

        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(jwtKey, "", timeout, TimeUnit.SECONDS);

        SetOperations setOperations = redisTemplate.opsForSet();
        setOperations.add(userSetKey, jwt);
    }

    /**
     * 从redis中删除指定用户的jwt令牌
     * @param userId
     */
    public void removeJwtFromRedis(String userId) {
        String userSetKey = USER_SET_KEY_PREFIX + userId;
        SetOperations setOperations = redisTemplate.opsForSet();
        Set<String> members = setOperations.members(userSetKey);
        for (String member : members) {
            redisTemplate.delete(JWT_KEY_PREFIX + member);
        }
        redisTemplate.delete(userSetKey);
    }

    /**
     * 从redis中删除指定用户的指定jwt令牌
     * @param userId
     * @param jwt
     */
    public void removeJwtFromRedis(String userId, String jwt) {
        String userSetKey = USER_SET_KEY_PREFIX + userId;
        SetOperations setOperations = redisTemplate.opsForSet();
        setOperations.remove(userSetKey, jwt);
        redisTemplate.delete(JWT_KEY_PREFIX + jwt);
    }

    /**
     * 检查redis中是否有指定用户的指定jwt令牌
     * @param userId
     * @param jwt
     */
    public boolean checkJwtInRedis(String userId, String jwt) {
        String userSetKey = USER_SET_KEY_PREFIX + userId;
        SetOperations setOperations = redisTemplate.opsForSet();
        Set<String> members = setOperations.members(userSetKey);
        return members != null && members.contains(jwt);
    }
}
