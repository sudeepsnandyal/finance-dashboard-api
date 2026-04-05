package com.finance.dashboard.repository;

import com.finance.dashboard.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>, JpaSpecificationExecutor<FinancialRecord> {

    // Basic queries
    List<FinancialRecord> findByUserIdAndDeletedFalseOrderByIdAsc(Long userId);
    List<FinancialRecord> findByUserIdAndDeletedFalseOrderByRecordDateDesc(Long userId);
    List<FinancialRecord> findByUserIdAndDeletedFalse(Long userId);
    Optional<FinancialRecord> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
    List<FinancialRecord> findByUserIdAndTypeAndDeletedFalse(Long userId, com.finance.dashboard.model.enums.RecordType type);
    List<FinancialRecord> findByUserIdAndCategoryAndDeletedFalse(Long userId, String category);
    List<FinancialRecord> findByUserIdAndRecordDateBetweenAndDeletedFalse(Long userId, LocalDate startDate, LocalDate endDate);
    long countByUserIdAndDeletedFalse(Long userId);
    List<FinancialRecord> findByIdLessThanAndUserIdAndDeletedFalseOrderByIdDesc(Long id, Long userId, org.springframework.data.domain.Pageable pageable);
    List<FinancialRecord> findByIdLessThanAndUserIdAndDeletedFalseOrderByRecordDateDesc(Long id, Long userId, org.springframework.data.domain.Pageable pageable);
    List<FinancialRecord> findByIdLessThanAndUserIdAndTypeAndDeletedFalse(Long id, Long userId, com.finance.dashboard.model.enums.RecordType type, org.springframework.data.domain.Pageable pageable);
    List<FinancialRecord> findByIdLessThanAndUserIdAndCategoryAndDeletedFalse(Long id, Long userId, String category, org.springframework.data.domain.Pageable pageable);
    List<FinancialRecord> findByIdLessThanAndUserIdAndRecordDateBetweenAndDeletedFalse(Long id, Long userId, LocalDate startDate, LocalDate endDate, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.userId = :userId AND f.type = 'INCOME' AND f.deleted = false")
    BigDecimal getTotalIncomeByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.userId = :userId AND f.type = 'EXPENSE' AND f.deleted = false")
    BigDecimal getTotalExpenseByUserId(@Param("userId") Long userId);

    @Query("SELECT f.category, SUM(f.amount) FROM FinancialRecord f WHERE f.userId = :userId AND f.type = :type AND f.deleted = false GROUP BY f.category")
    List<Object[]> getCategoryWiseTotals(@Param("userId") Long userId, @Param("type") com.finance.dashboard.model.enums.RecordType type);

    @Query(value = """
        SELECT DATE_FORMAT(record_date, '%Y-%m') as month,
               SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as income,
               SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as expense
        FROM financial_records
        WHERE user_id = :userId AND deleted = false AND record_date >= :startDate
        GROUP BY DATE_FORMAT(record_date, '%Y-%m')
        ORDER BY month DESC
        LIMIT 12
        """, nativeQuery = true)
    List<Object[]> getMonthlyTrends(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    @Query(value = """
        SELECT * FROM financial_records
        WHERE user_id = :userId AND deleted = false
        ORDER BY created_at DESC
        LIMIT 10
        """, nativeQuery = true)
    List<FinancialRecord> findRecentActivity(@Param("userId") Long userId);
}
