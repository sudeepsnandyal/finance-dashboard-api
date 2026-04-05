package com.finance.dashboard.controller;

import com.finance.dashboard.model.dto.ApiResponse;
import com.finance.dashboard.model.dto.DashboardSummaryDTO;
import com.finance.dashboard.service.DashboardService;
import com.finance.dashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.finance.dashboard.model.enums.RecordType;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<DashboardSummaryDTO> getSummary(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Long userId = getCurrentUserId();
        LocalDate actualStart = startDate != null ? startDate : LocalDate.now().minusMonths(1);
        LocalDate actualEnd = endDate != null ? endDate : LocalDate.now();

        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(userId, actualStart, actualEnd);
        return ApiResponse.success("Dashboard summary retrieved successfully", summary);
    }

    @GetMapping("/income/total")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<java.math.BigDecimal> getTotalIncome(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = getCurrentUserId();
        LocalDate actualStart = startDate != null ? startDate : LocalDate.now().minusMonths(1);
        LocalDate actualEnd = endDate != null ? endDate : LocalDate.now();

        return ApiResponse.success("Total income retrieved",
                dashboardService.getTotalIncome(userId, actualStart, actualEnd));
    }

    @GetMapping("/expense/total")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<java.math.BigDecimal> getTotalExpense(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = getCurrentUserId();
        LocalDate actualStart = startDate != null ? startDate : LocalDate.now().minusMonths(1);
        LocalDate actualEnd = endDate != null ? endDate : LocalDate.now();

        return ApiResponse.success("Total expense retrieved",
                dashboardService.getTotalExpense(userId, actualStart, actualEnd));
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<java.util.Map<String, java.math.BigDecimal>> getCategoryWiseTotals(
            @RequestParam(value = "type", required = false) com.finance.dashboard.model.enums.RecordType type) {
        Long userId = getCurrentUserId();
        com.finance.dashboard.model.enums.RecordType actualType = type != null ? type : com.finance.dashboard.model.enums.RecordType.EXPENSE;

        return ApiResponse.success("Category totals retrieved",
                dashboardService.getCategoryWiseTotals(userId, actualType));
    }

    @GetMapping("/trends")
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<java.util.List<com.finance.dashboard.model.dto.MonthlyTrendDTO>> getMonthlyTrends(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        Long userId = getCurrentUserId();
        LocalDate actualStart = startDate != null ? startDate : LocalDate.now().minusMonths(12);

        return ApiResponse.success("Monthly trends retrieved",
                dashboardService.getMonthlyTrends(userId, actualStart));
    }

    @GetMapping("/recent-activity")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
    public ApiResponse<java.util.List<com.finance.dashboard.model.dto.RecentActivityDTO>> getRecentActivity(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Long userId = getCurrentUserId();
        return ApiResponse.success("Recent activity retrieved",
                dashboardService.getRecentActivity(userId, limit));
    }

    private Long getCurrentUserId() {
        org.springframework.security.core.context.SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            throw new RuntimeException("Authentication required");
        }

        String username = context.getAuthentication().getName();
        return userService.getUserByUsername(username).getId();
    }
}
