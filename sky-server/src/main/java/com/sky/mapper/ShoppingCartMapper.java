package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 根据条件查询购物车
     * @param shoppingCart 查询条件对象
     * @return 符合条件的购物车项的ID列表
     */
    List<Long> getIdsByDTO(ShoppingCart shoppingCart);

    /**
     * 令指定的购物车项的数量加1
     *
     * @param id 购物车项的ID
     * @param num 增加的数量
     */
    @Update("update shopping_cart set number = number + #{num} where id = #{id}")
    void updateNumById(Long id, Integer num);

    /**
     * 添加购物车
     * @param shoppingCart 要添加的购物车项对象
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 查询当前用户的购物车
     * @param shoppingCart 查询条件对象，通常包含user_id
     * @return 当前用户的购物车项列表
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 删除购物车
     * @param id 要删除的购物车项的ID
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

    /**
     * 清空购物车
     * @param currentId 用户ID
     */
    @Delete("delete from shopping_cart where user_id = #{currentId}")
    void deleteByUserId(Long currentId);

    /**
     * 批量插入
     * @param shoppingCartList 要插入的购物车项列表
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}