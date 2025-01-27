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
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 新增套餐
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 修改套餐起售停售
     * @param status
     * @param id
     */
    void setStatus(Integer status, Long id);

    /**
     * 根据分类id查询已启用套餐
     * @param categoryId
     * @return
     */
    List<Setmeal> list(Long categoryId);

    /**
     * 根据id查询包含的菜品
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
