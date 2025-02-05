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
     * @param shoppingCartDTO 购物车数据传输对象，包含菜品或套餐信息
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
            shoppingcartMapper.updateNumById(id, 1);
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

    /**
     * 查询购物车
     * @return 购物车列表
     */
    public List<ShoppingCart> showShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        return shoppingcartMapper.list(shoppingCart);
    }

    /**
     * 删除购物车
     * @param shoppingCartDTO 购物车数据传输对象，包含要删除的菜品或套餐信息
     */
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 首先查询出目标购物车项id
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingcartMapper.list(shoppingCart);

        // 如果购物车中不存在，则直接返回
        if (list == null || list.isEmpty()) {
            return;
        }

        // 如果购物车中存在，且数量为1，则删除该购物车项
        if (list.get(0).getNumber() == 1) {
            shoppingcartMapper.deleteById(list.get(0).getId());
            return;
        }else {
            // 如果购物车中存在，且数量大于1，则数量减1
            shoppingcartMapper.updateNumById(list.get(0).getId(), -1);
        }

    }

    /**
     * 清空购物车
     */
    public void clean() {
        shoppingcartMapper.deleteByUserId(BaseContext.getCurrentId());
    }
}
