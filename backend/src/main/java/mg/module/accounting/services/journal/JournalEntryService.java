package mg.module.accounting.services.journal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.JournalEntryDto;
import mg.module.accounting.dto.JournalEntryLineDto;
import mg.module.accounting.models.AccountingPeriod;
import mg.module.accounting.models.JournalEntry;
import mg.module.accounting.models.JournalEntryLine;
import mg.module.accounting.services.account.AccountingPeriodService;

@Service
public class JournalEntryService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountingPeriodService accountingPeriodService;

    @Transactional
    public ApiResponse<JournalEntryDto> createJournalEntry(JournalEntryDto dto) {
        // check if period is locked
        AccountingPeriod period = accountingPeriodService.findPeriodForDate(LocalDate.now());
        if (period != null && period.isLocked()) {
            return ApiResponse.error("Cannot create entry in a locked period", "PERIOD_LOCKED");
        }

        // validate status
        if (!isValidStatus(dto.getStatus())) {
            return ApiResponse.error("Invalid status", "INVALID_STATUS");
        }

        // validate at least one debit and one credit
        boolean hasDebit = false, hasCredit = false;
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (var line : dto.getLines()) {
            if (line.getDebit() != null && line.getDebit().compareTo(BigDecimal.ZERO) > 0) {
                hasDebit = true;
                totalDebit = totalDebit.add(line.getDebit());
            }
            if (line.getCredit() != null && line.getCredit().compareTo(BigDecimal.ZERO) > 0) {
                hasCredit = true;
                totalCredit = totalCredit.add(line.getCredit());
            }
        }

        if (!hasDebit || !hasCredit) {
            return ApiResponse.error("At least one debit and one credit required", "INVALID_LINES");
        }

        // validate debit equals credit
        if (totalDebit.compareTo(totalCredit) != 0) {
            return ApiResponse.error("Debit and credit amounts must be equal", "UNBALANCED_ENTRY");
        }

        // create journal entry
        JournalEntry entry = new JournalEntry();
        entry.setDescription(dto.getDescription());
        entry.setStatus(dto.getStatus());
        entry.setUserId(dto.getUserId());
        entry.setPosted(false);
        entry.setCreatedAt(java.time.LocalDateTime.now());
        entry.setUpdatedAt(java.time.LocalDateTime.now());

        // create journal entry lines
        List<JournalEntryLine> lines = dto.getLines().stream().map(lineDto -> {
            JournalEntryLine line = new JournalEntryLine();
            line.setJournalEntry(entry);
            line.setAccountId(lineDto.getAccountId());
            line.setDebit(lineDto.getDebit() != null ? lineDto.getDebit() : BigDecimal.ZERO);
            line.setCredit(lineDto.getCredit() != null ? lineDto.getCredit() : BigDecimal.ZERO);
            return line;
        }).collect(Collectors.toList());

        entry.setLines(lines);
        entityManager.persist(entry);

        // convert to DTO
        JournalEntryDto responseDto = mapToDto(entry);
        return ApiResponse.success(responseDto, "Journal entry created successfully");
    }

    @Transactional
    public ApiResponse<JournalEntryDto> postJournalEntry(Long id) {
        JournalEntry entry = entityManager.find(JournalEntry.class, id);
        if (entry == null) {
            return ApiResponse.error("Journal entry not found", "ENTRY_NOT_FOUND");
        }
        // Handle null posted value for existing entries
        if (entry.isPosted() == null) {
            entry.setPosted(false);
        }
        if (entry.isPosted()) {
            return ApiResponse.error("Journal entry already posted", "ALREADY_POSTED");
        }
        if (!"VALIDATED".equals(entry.getStatus())) {
            return ApiResponse.error("Only validated entries can be posted", "INVALID_STATUS");
        }

        // check if period is locked
        AccountingPeriod period = accountingPeriodService.findPeriodForDate(entry.getCreatedAt().toLocalDate());
        if (period != null && period.isLocked()) {
            return ApiResponse.error("Cannot post entry in a locked period", "PERIOD_LOCKED");
        }

        entry.setPosted(true);
        entry.setUpdatedAt(java.time.LocalDateTime.now());
        entityManager.merge(entry);

        JournalEntryDto responseDto = mapToDto(entry);
        return ApiResponse.success(responseDto, "Journal entry posted successfully");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<JournalEntryDto>> getAllJournalEntries(boolean postedOnly) {
        String query = postedOnly
                ? "SELECT je FROM JournalEntry je WHERE je.posted = true"
                : "SELECT je FROM JournalEntry je";
        List<JournalEntry> entries = entityManager.createQuery(query, JournalEntry.class)
                .getResultList();
        List<JournalEntryDto> dtos = entries.stream().map(this::mapToDto).collect(Collectors.toList());
        return ApiResponse.success(dtos, "Journal entries retrieved successfully");
    }

    @Transactional(readOnly = true)
    public ApiResponse<JournalEntryDto> getJournalEntryById(Long id) {
        JournalEntry entry = entityManager.find(JournalEntry.class, id);
        if (entry == null) {
            return ApiResponse.error("Journal entry not found", "ENTRY_NOT_FOUND");
        }
        // Handle null posted value for existing entries
        if (entry.isPosted() == null) {
            entry.setPosted(false);
        }
        JournalEntryDto dto = mapToDto(entry);
        return ApiResponse.success(dto, "Journal entry retrieved successfully");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<JournalEntryDto>> searchJournalEntries(LocalDate startDate, LocalDate endDate, String status, Long userId, Long accountId, boolean postedOnly) {
        StringBuilder query = new StringBuilder("SELECT DISTINCT je FROM JournalEntry je LEFT JOIN je.lines jl WHERE 1=1");
        if (postedOnly) {
            query.append(" AND je.posted = true");
        }
        if (startDate != null) {
            query.append(" AND je.createdAt >= :startDate");
        }
        if (endDate != null) {
            query.append(" AND je.createdAt <= :endDate");
        }
        if (status != null && !status.isEmpty()) {
            query.append(" AND je.status = :status");
        }
        if (userId != null) {
            query.append(" AND je.userId = :userId");
        }
        if (accountId != null) {
            query.append(" AND jl.accountId = :accountId");
        }

        TypedQuery<JournalEntry> typedQuery = entityManager.createQuery(query.toString(), JournalEntry.class);
        if (startDate != null) {
            typedQuery.setParameter("startDate", startDate.atStartOfDay());
        }
        if (endDate != null) {
            typedQuery.setParameter("endDate", endDate.atTime(23, 59, 59));
        }
        if (status != null && !status.isEmpty()) {
            typedQuery.setParameter("status", status);
        }
        if (userId != null) {
            typedQuery.setParameter("userId", userId);
        }
        if (accountId != null) {
            typedQuery.setParameter("accountId", accountId);
        }

        List<JournalEntry> entries = typedQuery.getResultList();
        List<JournalEntryDto> dtos = entries.stream().map(this::mapToDto).collect(Collectors.toList());
        return ApiResponse.success(dtos, "Journal entries retrieved successfully");
    }

    private boolean isValidStatus(String status) {
        return status != null && List.of("CREATED", "VALIDATED", "CANCELED", "REVERSED").contains(status);
    }

    private JournalEntryDto mapToDto(JournalEntry entry) {
        JournalEntryDto dto = new JournalEntryDto();
        dto.setId(entry.getId());
        dto.setDescription(entry.getDescription());
        dto.setStatus(entry.getStatus());
        dto.setUserId(entry.getUserId());
        dto.setCreatedAt(entry.getCreatedAt());
        dto.setUpdatedAt(entry.getUpdatedAt());
        dto.setPosted(entry.isPosted() != null ? entry.isPosted() : false); 
        dto.setLines(entry.getLines().stream().map(this::mapToLineDto).collect(Collectors.toList()));
        return dto;
    }

    private JournalEntryLineDto mapToLineDto(JournalEntryLine line) {
        JournalEntryLineDto lineDto = new JournalEntryLineDto();
        lineDto.setId(line.getId());
        lineDto.setPrefix(line.getPrefix());
        lineDto.setAccountId(line.getAccountId());
        lineDto.setDebit(line.getDebit());
        lineDto.setCredit(line.getCredit());
        return lineDto;
    }
}