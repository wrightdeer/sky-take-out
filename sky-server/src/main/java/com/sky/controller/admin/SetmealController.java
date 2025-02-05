package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐：{}", setmealPageQueryDTO);
        PageResult pageQuery = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageQuery);
    }
    /**
     * 套餐批量删除
     * @param ids 要删除的套餐ID列表
     * @return 删除结果
     */
    @DeleteMapping
    @ApiOperation("套餐批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐：{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 新增套餐
     * @param setmealDTO 新增套餐的数据传输对象
     * @return 新增结果
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result add(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }
    /**
     * 根据id查询套餐
     * @param id 套餐ID
     * @return 查询到的套餐详情
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        SetmealVO setmeal = setmealService.getById(id);
        return Result.success(setmeal);
    }

    /**
     * 修改套餐
     * @param setmealDTO 修改套餐的数据传输对象
     * @return 修改结果
     */
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }
    /**
     * 套餐起售停售
     * @param status 套餐状态（0停售，1起售）
     * @param id 套餐ID
     * @return 起售停售结果
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("套餐起售停售：{}", status);
        setmealService.setStatus(status, id);
        return Result.success();
    }
}
