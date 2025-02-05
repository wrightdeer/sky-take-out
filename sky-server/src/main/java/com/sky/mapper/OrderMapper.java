package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param order 订单对象
     */
    void insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     * @return 订单对象
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders 订单对象
     */
    void update(Orders orders);

    /**
     * 根据用户id和订单号查询订单
     * @param userId 用户ID
     * @param orderNumber 订单号
     * @return 订单ID
     */
    @Select("select id from orders where user_id = #{userId} and number = #{orderNumber}")
    Long getIdByUserIdAndNumber(Long userId, String orderNumber);

    /**
     * 订单分页查询
     * @param ordersPageQueryDTO 订单分页查询条件
     * @return 订单列表
     */
    List<Orders> list(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id 订单ID
     * @return 订单对象
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 统计订单数量
     * @return 订单统计信息对象
     */
    OrderStatisticsVO statistics();

    /**
     * 查询指定状态和下单时间的订单
     * @param status 订单状态
     * @param orderTime 下单时间
     * @return 订单列表
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 根据日期范围查询营业额
     * @param begin 起始日期
     * @param end 结束日期
     * @return 具体日期与营业额映射
     */
    @MapKey("date")
    List<Map<String, Object>> getTurnoverByDays(@Param("begin") LocalDate begin, @Param("end") LocalDate end);

    /**
     * 根据日期范围查询订单数据
     * @param begin 起始日期
     * @param end 结束日期
     * @return 具体日期与订单数量映射
     */
    @MapKey("date")
    List<Map<String, Object>> getOrderDataByDays(LocalDate begin, LocalDate end);

    /**
     * 查询销量排名top10
     * @param begin 起始日期
     * @param end 结束日期
     * @return 销量排名top10
     */
    @MapKey("date")
    List<Map<String, Object>> getTop10(LocalDate begin, LocalDate end);
}