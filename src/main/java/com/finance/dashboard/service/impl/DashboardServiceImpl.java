package com.finance.dashboard.service.impl;

import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.enums.RecordType;
import com.finance.dashboard.model.dto.DashboardSummaryDTO;
import com.finance.dashboard.model.dto.MonthlyTrendDTO;
import com.finance.dashboard.model.dto.RecentActivityDTO;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository financialRecordRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryDTO getDashboardSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal totalIncome = getTotalIncome(userId, startDate, endDate);
        BigDecimal totalExpense = getTotalExpense(userId, startDate, endDate);

        DashboardSummaryDTO summary = DashboardSummaryDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(totalIncome.subtract(totalExpense))
                .categoryWiseTotals(getCategoryWiseTotals(userId, RecordType.EXPENSE))
                .monthlyTrends(getMonthlyTrends(userId, startDate))
                .recentActivity(getRecentActivity(userId, 10))
                .periodStart(startDate)
                .periodEnd(endDate)
                .build();

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalIncome(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal sum = financialRecordRepository.getTotalIncomeByUserId(userId);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpense(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal sum = financialRecordRepository.getTotalExpenseByUserId(userId);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getCategoryWiseTotals(Long userId, RecordType type) {
        List<Object[]> results = financialRecordRepository.getCategoryWiseTotals(userId, type);

        // Maintain insertion order for consistent display
        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();
        for (Object[] result : results) {
            String category = (String) result[0];
            BigDecimal total = (BigDecimal) result[1];
            categoryTotals.put(category, total);
        }

        return categoryTotals;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyTrendDTO> getMonthlyTrends(Long userId, LocalDate startDate) {
        List<Object[]> results = financialRecordRepository.getMonthlyTrends(userId, startDate);

        return results.stream()
                .map(result -> {
                    String monthStr = (String) result[0];
                    YearMonth yearMonth = YearMonth.parse(monthStr);
                    BigDecimal income = (BigDecimal) result[1];
                    BigDecimal expense = (BigDecimal) result[2];

                    return MonthlyTrendDTO.builder()
                            .month(yearMonth)
                            .income(income != null ? income : BigDecimal.ZERO)
                            .expense(expense != null ? expense : BigDecimal.ZERO)
                            .net(income != null && expense != null ? income.subtract(expense) : BigDecimal.ZERO)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecentActivityDTO> getRecentActivity(Long userId, int limit) {
        List<FinancialRecord> records = financialRecordRepository.findRecentActivity(userId);

        return records.stream()
                .limit(limit)
                .map(record -> RecentActivityDTO.builder()
                        .id(record.getId())
                        .type(record.getType())
                        .amount(record.getAmount())
                        .category(record.getCategory())
                        .recordDate(record.getRecordDate())
                        .notes(record.getNotes())
                        .build())
                .collect(Collectors.toList());
    }
}
