package com.finance.dashboard.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;

    @Builder.Default
    private Map<String, BigDecimal> categoryWiseTotals = Map.of();

    @Builder.Default
    private List<MonthlyTrendDTO> monthlyTrends = List.of();

    @Builder.Default
    private List<RecentActivityDTO> recentActivity = List.of();

    @Builder.Default
    private LocalDate periodStart = LocalDate.now().minusMonths(1);

    @Builder.Default
    private LocalDate periodEnd = LocalDate.now();
}
