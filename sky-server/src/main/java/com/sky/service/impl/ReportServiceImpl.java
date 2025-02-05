package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 统计营业额
     *
     * @param begin 开始日期
     * @param end 结束日期
     * @return 营业额统计结果
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<Map<String, Object>> turnoverData = orderMapper.getTurnoverByDays(begin, end);

        List<String> dateList = new ArrayList<>();
        List<String> turnoverList = new ArrayList<>();

        for (Map<String, Object> data : turnoverData) {
            String date = (String) data.get("date");
            Long turnover = ((Number) data.get("turnover")).longValue();
            dateList.add(date);
            turnoverList.add(String.valueOf(turnover));
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 统计用户数据
     *
     * @param begin 开始日期
     * @param end 结束日期
     * @return 用户数据统计结果
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<Map<String, Object>> userData = userMapper.getUserDataByDays(begin, end);
        List<String> dateList = new ArrayList<>();
        List<String> totalUserList = new ArrayList<>();
        List<String> newUserList = new ArrayList<>();

        for (int i = 1; i < userData.size(); i++) {
            Map<String, Object> map1 = userData.get(i);
            Map<String, Object> map2 = userData.get(i - 1);
            String date = (String) map1.get("date");
            Long totalUser = (Long) map1.get("totalUsers");
            Long newUser = (Long) map1.get("totalUsers") - (Long) map2.get("totalUsers");
            dateList.add(date);
            totalUserList.add(String.valueOf(totalUser));
            newUserList.add(String.valueOf(newUser));
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 统计订单数据
     *
     * @param begin 开始日期
     * @param end 结束日期
     * @return 订单数据统计结果
     */
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<Map<String, Object>> orderData = orderMapper.getOrderDataByDays(begin, end);
        List<String> dateList = new ArrayList<>();
        List<String> orderCountList = new ArrayList<>();
        List<String> validOrderCountList = new ArrayList<>();
        int totalOrderCount = 0;
        int totalValidOrderCount = 0;
        for (Map<String, Object> map : orderData) {
            String date = (String) map.get("date");
            Long orderCount = (Long) map.get("orderCount");
            Long validOrderCount = ((BigDecimal) map.get("validOrderCount")).longValue();

            dateList.add(date);
            orderCountList.add(String.valueOf(orderCount));
            validOrderCountList.add(String.valueOf(validOrderCount));

            totalOrderCount += orderCount.intValue();
            totalValidOrderCount += validOrderCount.intValue();
        }
        double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = (double) totalValidOrderCount / (double) totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(totalValidOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计销量排名
     *
     * @param begin 开始日期
     * @param end 结束日期
     * @return 销量排名统计结果
     */
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        List<Map<String, Object>> top10 = orderMapper.getTop10(begin, end);

        List<String> nameList = top10.stream()
                .map(map -> (String) map.get("name"))
                .collect(Collectors.toList());
        List<String> numberList = top10.stream()
                .map(map -> String.valueOf(map.get("number")))
                .collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }
}
