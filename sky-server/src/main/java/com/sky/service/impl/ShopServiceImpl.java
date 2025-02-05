package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {
    public static final String KEY = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 设置店铺状态
     * @param status 店铺状态，0表示关闭，1表示开启
     */
    public void setStatus(Integer status) {
        redisTemplate.opsForValue().set(KEY, status);
    }

    /**
     * 获取店铺状态
     * @return 店铺状态，0表示关闭，1表示开启
     */
    public Integer getStatus() {
        return  (Integer) redisTemplate.opsForValue().get(KEY);
    }
}