package com.finance.dashboard.model.dto;

import com.finance.dashboard.model.enums.RecordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentActivityDTO {
    private Long id;
    private RecordType type;
    private BigDecimal amount;
    private String category;
    private LocalDate recordDate;
    private String notes;
}
