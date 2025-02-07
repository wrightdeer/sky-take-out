package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
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
     * @param end   结束日期
     * @return 营业额统计结果
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<Map<String, Object>> turnoverData = orderMapper.getTurnoverByDays(begin, end);

        List<String> dateList = new ArrayList<>();
        List<String> turnoverList = new ArrayList<>();

        for (Map<String, Object> data : turnoverData) {
            String date = (String) data.get("date");
            Double turnover = ((BigDecimal) data.get("turnover")).doubleValue();
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
     * @param end   结束日期
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
     * @param end   结束日期
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
     * @param end   结束日期
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

    /**
     * 导出数据报表
     *
     * @param response 响应对象
     */
    public void exportBusinessData(HttpServletResponse response) throws IOException {
        // 查询数据库，获取营业数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        List<Map<String, Object>> turnoverData = orderMapper.getTurnoverByDays(begin, end);
        List<Map<String, Object>> userData = userMapper.getUserDataByDays(begin, end);
        List<Map<String, Object>> orderData = orderMapper.getOrderDataByDays(begin, end);
        Long totalUser = 0L;
        Long totalOrderCount = 0L;
        Long totalValidOrderCount = 0L;
        Double totalTurnover = 0.0;

        // 通过POI Excel工具类，将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(in);
        XSSFSheet sheet = workbook.getSheet("Sheet1");
        sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end + "营业数据");

        List<Map<String, Object>> processedData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            String date = turnoverData.get(i).get("date").toString();
            Double turnover = ((BigDecimal) turnoverData.get(i).get("turnover")).doubleValue();
            Long newUserCount = (Long) userData.get(i + 1).get("totalUsers") - (Long) userData.get(i).get("totalUsers");
            Long orderCount = (Long) orderData.get(i).get("orderCount");
            Long validOrderCount = ((BigDecimal) orderData.get(i).get("validOrderCount")).longValue();

            totalUser += newUserCount;
            totalOrderCount += orderCount;
            totalValidOrderCount += validOrderCount;
            totalTurnover += turnover;

            Double unitPrice = validOrderCount != 0 ? turnover / validOrderCount : 0.0;
            Double orderCompletionRate = orderCount != 0 ? (double) validOrderCount / (double) orderCount : 0.0;

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("date", date);
            rowMap.put("turnover", turnover);
            rowMap.put("validOrderCount", validOrderCount);
            rowMap.put("orderCompletionRate", orderCompletionRate);
            rowMap.put("unitPrice", unitPrice);
            rowMap.put("newUserCount", newUserCount);

            processedData.add(rowMap);
        }
        for (int i = 0; i < processedData.size(); i++) {
            XSSFRow row = sheet.getRow(i + 7);
            Map<String, Object> data = processedData.get(i);
            row.getCell(1).setCellValue((String) data.get("date"));
            row.getCell(2).setCellValue((Double) data.get("turnover"));
            row.getCell(3).setCellValue((Long) data.get("validOrderCount"));
            row.getCell(4).setCellValue((Double) data.get("orderCompletionRate"));
            row.getCell(5).setCellValue((Double) data.get("unitPrice"));
            row.getCell(6).setCellValue((Long) data.get("newUserCount"));
        }

        Double unitPrice = 0.0;
        if (totalValidOrderCount != 0) {
            unitPrice = totalTurnover / totalValidOrderCount;
        }
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = (double) totalValidOrderCount / (double) totalOrderCount;
        }
        Long newUserCount = (Long) userData.get(30).get("totalUsers") - (Long) userData.get(0).get("totalUsers");

        sheet.getRow(3).getCell(2).setCellValue(totalTurnover);
        sheet.getRow(4).getCell(2).setCellValue(totalValidOrderCount);
        sheet.getRow(3).getCell(4).setCellValue(orderCompletionRate);
        sheet.getRow(4).getCell(4).setCellValue(unitPrice);
        sheet.getRow(3).getCell(6).setCellValue(newUserCount);

        // 通过response将Excel文件输出到客户端浏览器
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }
}
