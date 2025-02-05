package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 分页查询条件
     * @return 分页查询结果
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 新增菜品
     * @param dishDTO 菜品信息
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 删除菜品
     * @param ids 菜品ID列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 起售停售
     * @param status 菜品状态（0：停售，1：起售）
     * @param id 菜品ID
     */
    void setStatus(Integer status, Long id);

    /**
     * 根据id查询菜品
     * @param id 菜品ID
     * @return 菜品详情
     */
    DishVO getById(Long id);

    /**
     * 修改菜品
     * @param dishDTO 菜品信息
     */
    void update(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品
     * @param categoryId 分类ID
     * @return 菜品列表
     */
    List<Dish> list(Long categoryId);

    /**
     * 根据分类id查询菜品及口味
     * 用户端接口，作缓存处理
     *
     * @param categoryId 分类ID
     * @return 菜品详情列表
     */
    List<DishVO> listWithFlavor(Long categoryId);
}