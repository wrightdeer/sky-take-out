package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServerImpl implements ShoppingCartServer {
    public static final int NUM_ONE = 1;
    @Autowired
    private ShoppingCartMapper shoppingcartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 先判断购物车中是否有相同的菜品（口味也应该相同）或套餐
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<Long> ids = shoppingcartMapper.getIdsByDTO(shoppingCart);

        // 如果购物车中存在，数量加1
        if (ids != null && ids.size() > 0) {
            // 获取购物车中菜品或套餐的id
            Long id = ids.get(0);
            // 令指定的菜品或套餐的数量加1
            shoppingcartMapper.addNumById(id);
            return;
        }

        // 如果购物车中不存在，新增一条数据
        Long dishId = shoppingCartDTO.getDishId();
        if (dishId != null) {
            // 添加菜品
            Dish dish = dishMapper.getById(dishId);
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        } else {
            // 添加套餐
            Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        }
        shoppingCart.setNumber(NUM_ONE);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingcartMapper.insert(shoppingCart);

    }
}
