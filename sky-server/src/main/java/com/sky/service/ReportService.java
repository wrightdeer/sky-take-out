package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 统计营业额
     * @param begin 开始日期
     * @param end 结束日期
     * @return 营业额统计结果
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计用户数据
     * @param begin 开始日期
     * @param end 结束日期
     * @return 用户数据统计结果
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计订单数据
     * @param begin 开始日期
     * @param end 结束日期
     * @return 订单数据统计结果
     */
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计销量排名
     * @param begin 开始日期
     * @param end 结束日期
     * @return 销量排名统计结果
     */
    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);
}