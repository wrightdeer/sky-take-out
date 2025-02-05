package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "用户端菜品接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 根据分类id查询菜品
     * @param categoryId 分类ID
     * @return 菜品列表
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(@RequestParam Long categoryId) {
        log.info("根据分类id查询菜品:{}", categoryId);
        List<DishVO> dishVOs = dishService.listWithFlavor(categoryId);
        return Result.success(dishVOs);
    }
}