package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

public interface ShoppingCartServer {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);
}
