package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入
     * @param flavors 待插入的菜品口味列表
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除对应的口味数据
     * @param ids 菜品ID列表
     */
    void deleteByDishIds(List<Long> ids);

    /**
     * 根据菜品id查询对应的口味数据
     * @param id 菜品ID
     * @return 该菜品对应的口味数据列表
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);
}