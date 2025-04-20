package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class JwtClearTask {
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 1 * * ?")
    public void processTimeOutJwt(){
        log.info("定时清理JWT");
        Set<String> userSetKeys = redisTemplate.keys("user:admin:*");
        if (userSetKeys != null && !userSetKeys.isEmpty()) {
            for (String userKey : userSetKeys) {
                Set<String> JwtSets = redisTemplate.opsForSet().members(userKey);
                if (JwtSets != null && !JwtSets.isEmpty()) {
                    for (String jwt : JwtSets) {
                        String jwtKey = "jwt:" + jwt;
                        boolean b = !redisTemplate.hasKey(jwtKey);
                        if (b) {
                            redisTemplate.opsForSet().remove(userKey, jwt);
                        }
                    }
                }
            }
        }
    }
}
