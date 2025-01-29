package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 根据条件查询购物车
     * @param shoppingCart
     * @return
     */
    List<Long> getIdsByDTO(ShoppingCart shoppingCart);

    /**
     * 令指定的购物车项的数量加1
     * @param id
     */
    @Update("update shopping_cart set number = number + 1 where id = #{id}")
    void addNumById(Long id);

    /**
     * 添加购物车
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);
}
