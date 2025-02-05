package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 分页查询
     * @param setmealPageQueryDTO 分页查询条件
     * @return 分页查询结果
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除
     * @param ids 要删除的套餐ID列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 新增套餐
     * @param setmealDTO 套餐信息
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐
     *
     * @param id 套餐ID
     * @return 套餐详细信息
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐
     * @param setmealDTO 套餐信息
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 修改套餐起售停售
     * @param status 状态码，1表示起售，0表示停售
     * @param id 套餐ID
     */
    void setStatus(Integer status, Long id);

    /**
     * 根据分类id查询已启用套餐
     * @param categoryId 分类ID
     * @return 已启用的套餐列表
     */
    List<Setmeal> list(Long categoryId);

    /**
     * 根据id查询包含的菜品
     * @param id 套餐ID
     * @return 菜品列表
     */
    List<DishItemVO> getDishItemById(Long id);
}