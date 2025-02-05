package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细数据
     * @param orderDetailList 订单明细列表
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查询订单明细
     * @param ordersId 订单ID
     * @return 订单明细列表
     */
    @Select("select * from order_detail where order_id = #{ordersId}")
    List<OrderDetail> getByOrderId(Long ordersId);
}