package com.sky.service;

public interface ShopService {
    /**
     * 设置店铺状态
     * @param status 店铺状态，0表示关闭，1表示营业
     */
    void setStatus(Integer status);

    /**
     * 获取店铺状态
     * @return 店铺状态，0表示关闭，1表示营业
     */
    Integer getStatus();
}