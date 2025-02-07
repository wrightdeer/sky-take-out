package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkspaceService {
    /**
     * 查询日运营数据
     * @return
     */
    BusinessDataVO getBusinessData();

    /**
     * 套餐总览
     * @return
     */
    SetmealOverViewVO overviewSetmeals();

    /**
     * 菜品总览
     * @return
     */
    DishOverViewVO overviewDishes();

    /**
     * 订单总览
     * @return
     */
    OrderOverViewVO overviewOrders();
}
