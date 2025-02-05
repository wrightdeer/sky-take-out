package com.sky.config;

import com.sky.properties.BaiduMapProperties;
import com.sky.utils.BaiduMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于加载百度地图工具类
 */
@Configuration
@Slf4j
public class BaiduMapConfiguration {

    @Bean
    public BaiduMapUtil baiduMapUtil(BaiduMapProperties baiduMapProperties){
        log.info("加载百度地图工具类：{}",baiduMapProperties);
        return new BaiduMapUtil(baiduMapProperties);
    }
}
