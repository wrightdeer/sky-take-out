package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 统计营业额
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        return null;
    }
}
