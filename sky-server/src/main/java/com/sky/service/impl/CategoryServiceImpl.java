package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 根据类型查询
     *
     * @param type
     * @param status
     * @return
     */
    public List<Category> list(Integer type, Integer status) {
        return categoryMapper.listQuery(type, status);
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 新增分类
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        category.setStatus(StatusConstant.DISABLE);

        categoryMapper.insert(category);
    }

    /**
     * 启用禁用分类
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();
        categoryMapper.update(category);
    }

    /**
     * 删除分类
     * @param id
     */
    public void delete(Long id) {
        // 检查是否关联了菜品或套餐
        Integer count = dishMapper.countByCategoryId(id);
        if (count > 0) {
            // 关联了菜品，抛出业务异常
            throw new DeletionNotAllowedException("当前分类下关联了菜品，不能删除");
        }
        count = setmealMapper.countByCategoryId(id);
        if (count > 0) {
            // 关联了套餐，抛出业务异常
            throw new DeletionNotAllowedException("当前分类下关联了套餐，不能删除");
        }
        categoryMapper.delete(id);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        categoryMapper.update(category);
    }

    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.listQuery(type,null);
    }
}
