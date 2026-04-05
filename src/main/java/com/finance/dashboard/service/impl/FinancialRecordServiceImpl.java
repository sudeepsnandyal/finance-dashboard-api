package com.finance.dashboard.service.impl;

import com.finance.dashboard.exception.AccessDeniedException;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.model.User;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.enums.RecordType;
import com.finance.dashboard.model.dto.FinancialRecordDTO;
import com.finance.dashboard.model.dto.PageResponse;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.service.CustomUserDetailsService;
import com.finance.dashboard.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    @Transactional
    public FinancialRecordDTO createRecord(FinancialRecordDTO recordDTO) {
        Long userId = getCurrentUserId();

        FinancialRecord record = FinancialRecord.builder()
                .userId(userId)
                .type(recordDTO.getType())
                .amount(recordDTO.getAmount())
                .category(recordDTO.getCategory())
                .recordDate(recordDTO.getRecordDate())
                .notes(recordDTO.getNotes())
                .deleted(false)
                .build();

        FinancialRecord saved = financialRecordRepository.save(record);
        return convertToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialRecordDTO getRecordById(Long id, Long userId) {
        FinancialRecord record = financialRecordRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));

        return convertToDTO(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecordDTO> getAllRecords(Long userId) {
        return financialRecordRepository.findByUserIdAndDeletedFalseOrderByRecordDateDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FinancialRecordDTO> getRecords(Long userId, Long cursor, int limit) {
        List<FinancialRecord> records;
        long total;

        if (cursor == null) {
            records = financialRecordRepository.findByUserIdAndDeletedFalseOrderByIdAsc(userId)
                    .stream()
                    .limit(limit)
                    .toList();
        } else {
            records = financialRecordRepository.findByIdLessThanAndUserIdAndDeletedFalseOrderByIdDesc(
                    cursor, userId, PageRequest.of(0, limit)
            );
        }

        total = financialRecordRepository.countByUserIdAndDeletedFalse(userId);

        List<FinancialRecordDTO> content = records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Long nextCursor = !records.isEmpty() && records.size() == limit ? records.get(records.size() - 1).getId() : null;

        return PageResponse.<FinancialRecordDTO>builder()
                .content(content)
                .page(cursor != null ? 1 : 0)
                .size(limit)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / limit))
                .last(nextCursor == null || records.size() < limit)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FinancialRecordDTO> getRecordsByType(Long userId, RecordType type, Long cursor, int limit) {
        List<FinancialRecord> records;

        if (cursor == null) {
            records = financialRecordRepository.findByUserIdAndTypeAndDeletedFalse(userId, type)
                    .stream()
                    .limit(limit)
                    .toList();
        } else {
            records = financialRecordRepository.findByIdLessThanAndUserIdAndTypeAndDeletedFalse(
                    cursor, userId, type, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))
            );
        }

        return buildPageResponse(records, limit, cursor);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FinancialRecordDTO> getRecordsByCategory(Long userId, String category, Long cursor, int limit) {
        List<FinancialRecord> records;

        if (cursor == null) {
            records = financialRecordRepository.findByUserIdAndCategoryAndDeletedFalse(userId, category)
                    .stream()
                    .limit(limit)
                    .toList();
        } else {
            records = financialRecordRepository.findByIdLessThanAndUserIdAndCategoryAndDeletedFalse(
                    cursor, userId, category, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))
            );
        }

        return buildPageResponse(records, limit, cursor);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FinancialRecordDTO> getRecordsByDateRange(Long userId, LocalDate startDate, LocalDate endDate, Long cursor, int limit) {
        List<FinancialRecord> records;

        if (cursor == null) {
            records = financialRecordRepository.findByUserIdAndRecordDateBetweenAndDeletedFalse(userId, startDate, endDate)
                    .stream()
                    .sorted((r1, r2) -> r2.getRecordDate().compareTo(r1.getRecordDate()))
                    .limit(limit)
                    .toList();
        } else {
            records = financialRecordRepository.findByIdLessThanAndUserIdAndRecordDateBetweenAndDeletedFalse(
                    cursor, userId, startDate, endDate, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))
            );
        }

        return buildPageResponse(records, limit, cursor);
    }

    @Override
    @Transactional
    public FinancialRecordDTO updateRecord(Long id, Long userId, FinancialRecordDTO recordDTO) {
        FinancialRecord record = financialRecordRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));

        record.setType(recordDTO.getType());
        record.setAmount(recordDTO.getAmount());
        record.setCategory(recordDTO.getCategory());
        record.setRecordDate(recordDTO.getRecordDate());
        record.setNotes(recordDTO.getNotes());

        FinancialRecord updated = financialRecordRepository.save(record);
        return convertToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteRecord(Long id, Long userId) {
        FinancialRecord record = financialRecordRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));

        financialRecordRepository.delete(record);
    }

    @Override
    @Transactional
    public FinancialRecordDTO softDeleteRecord(Long id, Long userId) {
        FinancialRecord record = financialRecordRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));

        record.setDeleted(true);
        FinancialRecord updated = financialRecordRepository.save(record);
        return convertToDTO(updated);
    }

    private PageResponse<FinancialRecordDTO> buildPageResponse(List<FinancialRecord> records, int limit, Long cursor) {
        List<FinancialRecordDTO> content = records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Long nextCursor = !records.isEmpty() && records.size() == limit ? records.get(records.size() - 1).getId() : null;

        return PageResponse.<FinancialRecordDTO>builder()
                .content(content)
                .page(cursor != null ? 1 : 0)
                .size(limit)
                .last(nextCursor == null || records.size() < limit)
                .build();
    }

    private FinancialRecordDTO convertToDTO(FinancialRecord record) {
        return FinancialRecordDTO.builder()
                .id(record.getId())
                .type(record.getType())
                .amount(record.getAmount())
                .category(record.getCategory())
                .recordDate(record.getRecordDate())
                .notes(record.getNotes())
                .userId(record.getUserId())
                .build();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        return user.getId();
    }
}
