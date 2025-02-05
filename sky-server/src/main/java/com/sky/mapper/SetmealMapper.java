package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    /**
     * 根据分类id查询套餐的数量
     * @param categoryId 分类id
     * @return 套餐数量
     */
    @Select("select count(id) from setmeal where category_id = #{id}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 分页查询
     * @param setmealPageQueryDTO 分页查询条件
     * @return 分页结果
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id与状态查询套餐的数量
     * @param ids 套餐id列表
     * @param status 状态
     * @return 套餐数量
     */
    Long countByStatusAndIds(List<Long> ids, Integer status);

    /**
     * 批量删除套餐
     * @param ids 套餐id列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 新增套餐
     * @param setmeal 套餐对象
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return 套餐对象
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 修改套餐
     * @param setmeal 套餐对象
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 根据分类id查询已启售套餐
     * @param categoryId 分类id
     * @return 启售套餐列表
     */
    @Select("select * from setmeal where category_id = #{categoryId} and status = 1")
    List<Setmeal> list(Long categoryId);

    /**
     * 根据套餐id查询菜品项
     * @param setmealId 套餐id
     * @return 菜品项列表
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd inner join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}