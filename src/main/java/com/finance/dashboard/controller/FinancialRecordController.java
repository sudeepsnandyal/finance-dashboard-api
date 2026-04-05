package com.finance.dashboard.controller;

import com.finance.dashboard.model.dto.ApiResponse;
import com.finance.dashboard.model.dto.FinancialRecordDTO;
import com.finance.dashboard.model.dto.PageResponse;
import com.finance.dashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    @PostMapping
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<FinancialRecordDTO> createRecord(@Valid @RequestBody FinancialRecordDTO recordDTO) {
        FinancialRecordDTO created = financialRecordService.createRecord(recordDTO);
        return ApiResponse.success("Record created successfully", created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<FinancialRecordDTO> getRecord(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        FinancialRecordDTO record = financialRecordService.getRecordById(id, userId);
        return ApiResponse.success("Record retrieved successfully", record);
    }

    @GetMapping
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<FinancialRecordDTO>> getAllRecords(
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        Long userId = getCurrentUserId();
        PageResponse<FinancialRecordDTO> records = financialRecordService.getRecords(userId, cursor, limit);
        return ApiResponse.success("Records retrieved successfully", records);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<FinancialRecordDTO>> getRecordsByType(
            @PathVariable com.finance.dashboard.model.enums.RecordType type,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        Long userId = getCurrentUserId();
        PageResponse<FinancialRecordDTO> records = financialRecordService.getRecordsByType(userId, type, cursor, limit);
        return ApiResponse.success("Records retrieved successfully", records);
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<FinancialRecordDTO>> getRecordsByCategory(
            @PathVariable String category,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        Long userId = getCurrentUserId();
        PageResponse<FinancialRecordDTO> records = financialRecordService.getRecordsByCategory(userId, category, cursor, limit);
        return ApiResponse.success("Records retrieved successfully", records);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<FinancialRecordDTO>> getRecordsByDateRange(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        Long userId = getCurrentUserId();
        PageResponse<FinancialRecordDTO> records = financialRecordService.getRecordsByDateRange(userId, startDate, endDate, cursor, limit);
        return ApiResponse.success("Records retrieved successfully", records);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<FinancialRecordDTO> updateRecord(@PathVariable Long id, @Valid @RequestBody FinancialRecordDTO recordDTO) {
        Long userId = getCurrentUserId();
        FinancialRecordDTO updated = financialRecordService.updateRecord(id, userId, recordDTO);
        return ApiResponse.success("Record updated successfully", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<Void> deleteRecord(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        financialRecordService.deleteRecord(id, userId);
        return ApiResponse.success("Record deleted successfully", null);
    }

    @PostMapping("/{id}/soft-delete")
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<FinancialRecordDTO> softDeleteRecord(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        FinancialRecordDTO deleted = financialRecordService.softDeleteRecord(id, userId);
        return ApiResponse.success("Record soft deleted successfully", deleted);
    }

    private Long getCurrentUserId() {
        org.springframework.security.core.context.SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            throw new RuntimeException("Authentication required");
        }

        // The principal is UserDetails, and since we extend User with getAuthorities,
        // we can get the username and fetch the user from DB to get the ID
        String username = context.getAuthentication().getName();
        return userService.getUserByUsername(username).getId();
    }

    private final com.finance.dashboard.service.UserService userService;
}
