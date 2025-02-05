package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartServer {
    /**
     * 添加购物车
     * @param shoppingCartDTO 购物车数据传输对象，包含要添加的商品信息
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查询购物车
     * @return 购物车中的商品列表
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 删除购物车中的商品
     * @param shoppingCartDTO 购物车数据传输对象，包含要删除的商品信息
     */
    void sub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void clean();
}