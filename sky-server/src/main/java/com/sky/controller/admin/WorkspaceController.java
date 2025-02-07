package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Api(tags = "工作台相关接口")
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 获取今日运营数据
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("获取今日运营数据")
    public Result<BusinessDataVO> getBusinessData(){
        log.info("获取今日运营数据数据");
        BusinessDataVO businessDataVO = workspaceService.getBusinessData();
        return Result.success(businessDataVO);
    }
    @GetMapping("/overviewSetmeals")
    @ApiOperation("获取套餐总览")
    public Result<SetmealOverViewVO> overviewSetmeals(){
        log.info("获取套餐总览");
        SetmealOverViewVO setmealOverViewVO = workspaceService.overviewSetmeals();
        return Result.success(setmealOverViewVO);
    }
    @GetMapping("/overviewDishes")
    @ApiOperation("获取菜品总览")
    private Result<DishOverViewVO> overviewDishes(){
        log.info("获取菜品总览");
        DishOverViewVO dishOverViewVO = workspaceService.overviewDishes();
        return Result.success(dishOverViewVO);
    }
    @GetMapping("/overviewOrders")
    @ApiOperation("获取订单总览")
    private Result<OrderOverViewVO> overviewOrders(){
        log.info("获取订单总览");
        OrderOverViewVO orderOverViewVO = workspaceService.overviewOrders();
        return Result.success(orderOverViewVO);
    }
}
