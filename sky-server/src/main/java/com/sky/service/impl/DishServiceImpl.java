package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    private void cleanCache() {
        Set keys = redisTemplate.keys("dish*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO 分页查询条件
     * @return 分页查询结果
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        List<DishVO> dishVOS = page.getResult();
//        for (DishVO dishVO : dishVOS) {
//            String categoryName = categoryMapper.getById(dishVO.getCategoryId()).getName();
//            dishVO.setCategoryName(categoryName);
//        }

        return new PageResult(page.getTotal(), dishVOS);
    }

    /**
     * 新增菜品
     *
     * @param dishDTO 菜品数据传输对象
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        // 插入菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        Long dishId = dish.getId();
        // 插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(flavors);
        }
//        // 删除缓存数据
//        String key = "dish_" + dishDTO.getCategoryId();
//        redisTemplate.delete(key);
    }

    /**
     * 批量删除菜品
     *
     * @param ids 菜品ID列表
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断菜品是否能够删除-菜品是否在起售中
        Long numOfOnSale = dishMapper.countByStatusAndIds(ids, StatusConstant.ENABLE);
        if (numOfOnSale > 0) {
            // 不允许删除，抛出业务异常
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        // 判断菜品是否能够删除-菜品是否关联了套餐
        Long numOfAssociatedSetmeal = setmealDishMapper.countByDishIds(ids);
        if (numOfAssociatedSetmeal > 0) {
            // 不允许删除，抛出业务异常
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品表中的数据
        dishMapper.deleteBatch(ids);

        // 删除口味表中的数据
        dishFlavorMapper.deleteByDishIds(ids);

//        // 删除全部缓存数据
//        Set key = redisTemplate.keys("dish_*");
//        if (key != null) {
//            redisTemplate.delete(key);
//        }
    }

    /**
     * 起售/停售菜品
     *
     * @param status 菜品状态
     * @param id     菜品ID
     */
    public void setStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
        // 删除缓存数据 因为此处不方便获取是哪个分类下的菜品被删除了，所以删除全部缓存数据
        cleanCache();
    }

    /**
     * 根据id查询菜品
     *
     * @param id 菜品ID
     * @return 菜品详情
     */
    public DishVO getById(Long id) {
        Dish dish = dishMapper.getById(id);
        // 根据菜品id查询对应的口味
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 修改菜品
     *
     * @param dishDTO 菜品数据传输对象
     */
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 修改菜品数据
        dishMapper.update(dish);

        // 根据菜品id删除口味数据
        dishFlavorMapper.deleteByDishIds(Collections.singletonList(dishDTO.getId()));

        // 重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }

        // 删除缓存数据 因为可能修改菜品的分类，受影响的分类有两个，所以要删除全部缓存数据
        cleanCache();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类ID
     * @return 菜品列表
     */
    public List<Dish> list(Long categoryId) {
        return dishMapper.list(categoryId);
    }

    /**
     * 根据id查询菜品及口味
     * 用户端接口，作缓存处理
     *
     * @param categoryId 分类ID
     * @return 菜品详情列表
     */
    public List<DishVO> listWithFlavor(Long categoryId) {
        // 从redis中获取菜品数据
        String key = "dish" + categoryId;
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);

        // 如果存在，则直接返回
        if (dishVOList != null && dishVOList.size() > 0) {
            log.info("菜品数据从redis中获取");
            return dishVOList;
        }

        // 如果不存在，则查询数据库
        dishVOList = new ArrayList<>();
        List<Dish> dishList = dishMapper.list(categoryId);
        if (dishList != null && dishList.size() > 0) {
            for (Dish dish : dishList) {
                if (!Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                    continue;
                }
                List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dish.getId());
                DishVO dishVO = new DishVO();
                BeanUtils.copyProperties(dish, dishVO);
                dishVO.setFlavors(flavors);
                dishVOList.add(dishVO);
            }
        }
        // 将菜品数据保存到redis中
        log.info("菜品数据从数据库中获取");
        redisTemplate.opsForValue().set(key, dishVOList, 60, TimeUnit.MINUTES);

        return dishVOList;
    }
}
