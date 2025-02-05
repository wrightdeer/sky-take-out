package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 根据类型查询
     *
     * @param type 分类类型
     * @param status 状态
     * @return 分类列表
     */
    List<Category> list(Integer type, Integer status);

    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO 查询条件
     * @return 分页数量与列表数据
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     *
     * @param categoryDTO 分类信息
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 启用禁用分类
     *
     * @param status 状态
     * @param id 分类id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 删除分类
     *
     * @param id 分类id
     */
    void delete(Long id);

    /**
     * 修改分类
     *
     * @param categoryDTO 分类信息
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 根据类型查询
     * @param type 分类类型
     * @return 分类列表
     */
    List<Category> list(Integer type);
}
