package com.finance.dashboard.service;

import com.finance.dashboard.model.dto.DashboardSummaryDTO;
import com.finance.dashboard.model.dto.MonthlyTrendDTO;
import com.finance.dashboard.model.dto.RecentActivityDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    DashboardSummaryDTO getDashboardSummary(Long userId, LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalIncome(Long userId, LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalExpense(Long userId, LocalDate startDate, LocalDate endDate);
    Map<String, BigDecimal> getCategoryWiseTotals(Long userId, com.finance.dashboard.model.enums.RecordType type);
    List<MonthlyTrendDTO> getMonthlyTrends(Long userId, LocalDate startDate);
    List<RecentActivityDTO> getRecentActivity(Long userId, int limit);
}
