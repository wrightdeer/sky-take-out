package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单管理接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 条件查询订单
     * @param ordersPageQueryDTO 订单分页查询条件
     * @return 分页查询结果
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> pageQuery( OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("分页查询订单：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 统计订单数量
     * @return 订单统计结果
     */
    @GetMapping("/statistics")
    @ApiOperation("统计订单数量")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }
    /**
     * 订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/details/{id}")
    @ApiOperation("订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id){
        log.info("订单详情查询：{}", id);
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }
    /**
     * 接单
     * @param orders 订单信息
     * @return 操作结果
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody Orders orders){
        orderService.confirm(orders.getId());
        return Result.success();
    }
    /**
     * 派送订单
     * @param id 订单ID
     * @return 操作结果
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO 拒单信息
     * @return 操作结果
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }
    /**
     * 完成订单
     * @param id 订单ID
     * @return 操作结果
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }
    /**
     * 取消订单
     * @param ordersCancelDTO 取消订单信息
     * @return 操作结果
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }
}
