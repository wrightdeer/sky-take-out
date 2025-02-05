package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.io.IOException;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO 下单信息数据传输对象
     * @return 订单提交结果视图对象
     * @throws IOException 可能抛出的IO异常
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) throws IOException;

    /**
     * 订单支付
     * @param ordersPaymentDTO 支付信息数据传输对象
     * @return 订单支付结果视图对象
     * @throws Exception 可能抛出的异常
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo 外部交易号
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @param page 当前页码
     * @param pageSize 每页大小
     * @param status 订单状态
     * @return 分页查询结果
     */
    PageResult pageQuery(int page, int pageSize, Integer status);

    /**
     * 根据订单id查询订单详情
     * @param id 订单ID
     * @return 订单详情视图对象
     */
    OrderVO getOrderDetail(Long id);

    /**
     * 用户取消订单
     * @param id 订单ID
     */
    void cancel(Long id);

    /**
     * 分页查询
     * @param ordersPageQueryDTO 分页查询条件数据传输对象
     * @return 分页查询结果
     */
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 再来一单
     * @param id 订单ID
     */
    void repetition(Long id);

    /**
     * 统计订单数据
     * @return 订单统计数据视图对象
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     * @param id 订单ID
     */
    void confirm(Long id);

    /**
     * 派送订单
     * @param id 订单ID
     */
    void delivery(Long id);

    /**
     * 拒绝订单
     * @param ordersRejectionDTO 拒绝订单信息数据传输对象
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 完成订单
     * @param id 订单ID
     */
    void complete(Long id);

    /**
     * 取消订单
     * @param ordersCancelDTO 取消订单信息数据传输对象
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 催单
     * @param id 订单ID
     */
    void reminder(Long id);
}