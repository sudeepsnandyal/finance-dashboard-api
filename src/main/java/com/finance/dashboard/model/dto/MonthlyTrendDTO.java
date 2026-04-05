package com.finance.dashboard.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrendDTO {
    private YearMonth month;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;
}
