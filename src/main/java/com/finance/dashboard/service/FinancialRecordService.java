package com.finance.dashboard.service;

import com.finance.dashboard.model.dto.FinancialRecordDTO;
import com.finance.dashboard.model.dto.PageResponse;

import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordService {
    FinancialRecordDTO createRecord(FinancialRecordDTO recordDTO);
    FinancialRecordDTO getRecordById(Long id, Long userId);
    List<FinancialRecordDTO> getAllRecords(Long userId);
    PageResponse<FinancialRecordDTO> getRecords(Long userId, Long cursor, int limit);
    PageResponse<FinancialRecordDTO> getRecordsByType(Long userId, com.finance.dashboard.model.enums.RecordType type, Long cursor, int limit);
    PageResponse<FinancialRecordDTO> getRecordsByCategory(Long userId, String category, Long cursor, int limit);
    PageResponse<FinancialRecordDTO> getRecordsByDateRange(Long userId, LocalDate startDate, LocalDate endDate, Long cursor, int limit);
    FinancialRecordDTO updateRecord(Long id, Long userId, FinancialRecordDTO recordDTO);
    void deleteRecord(Long id, Long userId);
    FinancialRecordDTO softDeleteRecord(Long id, Long userId);
}
