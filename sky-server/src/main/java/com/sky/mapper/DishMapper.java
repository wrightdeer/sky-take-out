package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     * @param categoryId 分类id
     * @return 该分类下的菜品数量
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO 分页查询条件
     * @return 分页查询结果
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 新增菜品
     * @param dish 菜品信息
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 查询指定id菜品处于启售状态的数量
     * @param ids 菜品id列表
     * @param status 状态码
     * @return 指定状态下的菜品数量
     */
    Long countByStatusAndIds(List<Long> ids, int status);

    /**
     * 根据id批量删除菜品
     * @param ids 菜品id列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id修改菜品
     * @param dish 菜品信息
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据id查询菜品
     * @param id 菜品id
     * @return 菜品信息
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据分类id查询菜品
     * @param categoryId 分类id
     * @return 该分类下的菜品列表
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> list(Long categoryId);
}