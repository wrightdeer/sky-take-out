package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据类型查询分类
     * @param type 类型
     * @return 分类列表
     */
    @ApiOperation("根据类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        return Result.success(categoryService.list(type));
    }

    /**
     *  分页查询
     * @param categoryPageQueryDTO 分类分页查询条件
     * @return 分页数量与列表数据
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询分类：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增分类
     * @param categoryDTO 分类信息
     * @return 成功信号
     */
    @ApiOperation("新增分类")
    @PostMapping
    public Result add(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 启用禁用分类
     * @param status 状态
     * @param id 分类id
     * @return 成功信号
     */
    @ApiOperation("启用禁用分类")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用禁用分类：{}, {}", status, id);
        categoryService.startOrStop(status, id);
        return Result.success();
    }
    /**
     * 删除分类
     * @param id 分类id
     * @return 成功信号
     */
    @ApiOperation("删除分类")
    @DeleteMapping
    public Result delete(Long id){
        log.info("删除分类：{}", id);
        categoryService.delete(id);
        return Result.success();
    }

    /**
     * 修改分类
     * @param categoryDTO 分类信息
     * @return 成功信号
     */
    @ApiOperation("修改分类")
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }
}
