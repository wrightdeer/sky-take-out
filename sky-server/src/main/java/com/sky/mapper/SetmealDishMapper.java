package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询套餐数量
     * @param ids 菜品id列表
     * @return 套餐数量
     */
    Long countByDishIds(List<Long> ids);

    /**
     * 批量插入套餐菜品关系
     * @param setmealDishes 套餐菜品关系列表
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询套餐菜品关系
     * @param id 套餐id
     * @return 套餐菜品关系列表
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    /**
     * 根据套餐id删除套餐菜品关系
     * @param id 套餐id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);
}