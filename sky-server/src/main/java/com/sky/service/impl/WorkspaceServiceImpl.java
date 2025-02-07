package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 查询今日运营数据
     *
     * @return
     */
    public BusinessDataVO getBusinessData() {
        LocalDate date = LocalDate.now();

        double turnover = ((BigDecimal) orderMapper.getTurnoverByDays(date, date).get(0).get("turnover")).doubleValue();

        Map<String, Object> map = orderMapper.getOrderDataByDays(date, date).get(0);
        int validOrderCount = ((BigDecimal) map.get("validOrderCount")).intValue();
        int orderCount = ((Long) map.get("orderCount")).intValue();

        double orderCompletionRate = 0.0;
        if (orderCount != 0) {
            orderCompletionRate = (double) validOrderCount / (double) orderCount;
        }

        double unitPrice = 0.0;
        if (validOrderCount != 0) {
            unitPrice = turnover / validOrderCount;
        }

        Integer newUsers = userMapper.getUserNumByDays(date);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 套餐总览
     * @return
     */
    public SetmealOverViewVO overviewSetmeals() {
        Long sold = setmealMapper.countByStatusAndIds(null, 1);
        Long discontinued = setmealMapper.countByStatusAndIds(null, 0);
        return SetmealOverViewVO.builder()
                .sold(sold.intValue())
                .discontinued(discontinued.intValue())
                .build();
    }

    /**
     * 菜品总览
     * @return
     */
    public DishOverViewVO overviewDishes() {
        Long sold = dishMapper.countByStatusAndIds(null, 1);
        Long discontinued = dishMapper.countByStatusAndIds(null, 0);
        return DishOverViewVO.builder()
                .sold(sold.intValue())
                .discontinued(discontinued.intValue())
                .build();
    }

    /**
     * 订单总览
     * @return
     */
    public OrderOverViewVO overviewOrders() {
        LocalDate date = LocalDate.now();
        Integer waitingOrders = orderMapper.countByStatusAndDate(Orders.TO_BE_CONFIRMED, date);
        Integer deliveredOrders = orderMapper.countByStatusAndDate(Orders.DELIVERY_IN_PROGRESS, date);
        Integer completedOrders = orderMapper.countByStatusAndDate(Orders.COMPLETED, date);
        Integer cancelledOrders = orderMapper.countByStatusAndDate(Orders.CANCELLED, date);
        Integer allOrders = waitingOrders + deliveredOrders + completedOrders + cancelledOrders;
        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .allOrders(allOrders)
                .build();
    }
}
