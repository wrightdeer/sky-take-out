package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "用户端分类接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据类型查询分类
     * @param type 分类类型
     * @return 包含指定类型的启用状态分类列表的结果对象
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type) {
        log.info("查询分类:{}", type);
        List<Category> list = categoryService.list(type, StatusConstant.ENABLE);
        return Result.success(list);
    }
}
