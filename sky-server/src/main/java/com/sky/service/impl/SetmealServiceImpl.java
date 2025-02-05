package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 分页查询
     * @param setmealPageQueryDTO 分页查询条件DTO
     * @return 分页查询结果
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        List<SetmealVO> result = page.getResult();
        return new PageResult(page.getTotal(), result);
    }

    /**
     * 批量删除
     * @param ids 要删除的套餐ID列表
     */
    public void deleteBatch(List<Long> ids) {
        // 判断套餐是否能够删除-套餐是否在起售中
        Long numOfOnSale = setmealMapper.countByStatusAndIds(ids, StatusConstant.ENABLE);
        if (numOfOnSale > 0) {
            // 不允许删除，抛出业务异常
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        setmealMapper.deleteBatch(ids);
    }

    /**
     * 新增套餐
     * @param setmealDTO 套餐DTO
     */
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.insert(setmeal);
        Long id = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(id);
            }
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 根据id查询套餐
     *
     * @param id 套餐ID
     * @return 套餐VO
     */
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishMapper.getBySetmealId(id));
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO 套餐DTO
     */
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        // 先删除套餐中的菜品数据
        setmealDishMapper.deleteBySetmealId(setmeal.getId());

        // 重新插入套餐中的菜品数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
            }
            setmealDishMapper.insertBatch(setmealDishes);
        }

    }

    /**
     * 套餐起售停售
     * @param status 状态码，1表示起售，0表示停售
     * @param id 套餐ID
     */
    public void setStatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 根据分类id查询已启用套餐
     * @param categoryId 分类ID
     * @return 已启用的套餐列表
     */
    public List<Setmeal> list(Long categoryId){
        return setmealMapper.list(categoryId);
    }

    /**
     * 根据套餐id查询包含的菜品
     * @param id 套餐ID
     * @return 菜品列表
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
