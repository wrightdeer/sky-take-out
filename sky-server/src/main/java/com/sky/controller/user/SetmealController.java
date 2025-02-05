package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "用户端套餐接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类id查询套餐
     * @param categoryId 分类ID
     * @return 包含指定分类ID的套餐列表的Result对象
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId") //缓存套餐数据
    public Result<List<Setmeal>> list(Long categoryId) {
        log.info("根据分类id查询套餐:{}", categoryId);
        List<Setmeal> list = setmealService.list(categoryId);
        return Result.success(list);
    }
    /**
     * 根据套餐id查询包含的菜品
     * @param id 套餐ID
     * @return 包含指定套餐ID的菜品列表的Result对象
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品")
    public Result<List<DishItemVO>> getById(@PathVariable Long id) {
        log.info("根据套餐id查询包含的菜品:{}", id);
        List<DishItemVO> dishItemVOList = setmealService.getDishItemById(id);
        return Result.success(dishItemVOList);
    }
}
