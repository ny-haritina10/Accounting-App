package mg.module.accounting.services.journal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.JournalEntryAuditDto;
import mg.module.accounting.dto.JournalEntryDto;
import mg.module.accounting.dto.JournalEntryLineDto;
import mg.module.accounting.models.AccountingPeriod;
import mg.module.accounting.models.JournalEntry;
import mg.module.accounting.models.JournalEntryAudit;
import mg.module.accounting.models.JournalEntryLine;
import mg.module.accounting.models.JournalEntrySequence;
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
        if (period == null) {
            return ApiResponse.error("No accounting period found for current date", "NO_PERIOD_FOUND");
        }
        if (period.isLocked()) {
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

        // generate entry number
        String entryNumber = generateEntryNumber(period);

        // create journal entry
        JournalEntry entry = new JournalEntry();
        entry.setDescription(dto.getDescription());
        entry.setStatus(dto.getStatus());
        entry.setUserId(dto.getUserId());
        entry.setPosted(false);
        entry.setCreatedAt(java.time.LocalDateTime.now());
        entry.setUpdatedAt(java.time.LocalDateTime.now());
        entry.setEntryNumber(entryNumber);

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

        // log audit
        logAudit(entry.getId(), "CREATED", dto.getUserId(), "Created journal entry: " + entryNumber);

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

        // log audit
        logAudit(entry.getId(), "POSTED", entry.getUserId(), "Posted journal entry: " + entry.getEntryNumber());

        JournalEntryDto responseDto = mapToDto(entry);
        return ApiResponse.success(responseDto, "Journal entry posted successfully");
    }

    @Transactional
    public ApiResponse<JournalEntryDto> validateJournalEntry(Long id) {
        JournalEntry entry = entityManager.find(JournalEntry.class, id);
        if (entry == null) {
            return ApiResponse.error("Journal entry not found", "ENTRY_NOT_FOUND");
        }
        if (entry.isPosted() == null) {
            entry.setPosted(false);
        }
        if (entry.isPosted()) {
            return ApiResponse.error("Cannot validate a posted entry", "ALREADY_POSTED");
        }
        if (!"CREATED".equals(entry.getStatus())) {
            return ApiResponse.error("Only created entries can be validated", "INVALID_STATUS");
        }

        // check if period is locked
        AccountingPeriod period = accountingPeriodService.findPeriodForDate(entry.getCreatedAt().toLocalDate());
        if (period != null && period.isLocked()) {
            return ApiResponse.error("Cannot validate entry in a locked period", "PERIOD_LOCKED");
        }

        entry.setStatus("VALIDATED");
        entry.setUpdatedAt(java.time.LocalDateTime.now());
        entityManager.merge(entry);

        // log audit
        logAudit(entry.getId(), "VALIDATED", entry.getUserId(), "Validated journal entry: " + entry.getEntryNumber());

        JournalEntryDto responseDto = mapToDto(entry);
        return ApiResponse.success(responseDto, "Journal entry validated successfully");
    }

    @Transactional
    public ApiResponse<JournalEntryDto> cancelJournalEntry(Long id) {
        JournalEntry entry = entityManager.find(JournalEntry.class, id);
        if (entry == null) {
            return ApiResponse.error("Journal entry not found", "ENTRY_NOT_FOUND");
        }
        if (entry.isPosted() == null) {
            entry.setPosted(false);
        }
        if (entry.isPosted()) {
            return ApiResponse.error("Cannot cancel a posted entry", "ALREADY_POSTED");
        }
        if ("CANCELED".equals(entry.getStatus()) || "REVERSED".equals(entry.getStatus())) {
            return ApiResponse.error("Entry is already canceled or reversed", "INVALID_STATUS");
        }

        // check if period is locked
        AccountingPeriod period = accountingPeriodService.findPeriodForDate(entry.getCreatedAt().toLocalDate());
        if (period != null && period.isLocked()) {
            return ApiResponse.error("Cannot cancel entry in a locked period", "PERIOD_LOCKED");
        }

        entry.setStatus("CANCELED");
        entry.setUpdatedAt(java.time.LocalDateTime.now());
        entityManager.merge(entry);

        // log audit
        logAudit(entry.getId(), "CANCELED", entry.getUserId(), "Canceled journal entry: " + entry.getEntryNumber());

        JournalEntryDto responseDto = mapToDto(entry);
        return ApiResponse.success(responseDto, "Journal entry canceled successfully");
    }

    @Transactional
    public ApiResponse<JournalEntryDto> reverseJournalEntry(Long id) {
        JournalEntry originalEntry = entityManager.find(JournalEntry.class, id);
        if (originalEntry == null) {
            return ApiResponse.error("Journal entry not found", "ENTRY_NOT_FOUND");
        }
        if (originalEntry.isPosted() == null) {
            originalEntry.setPosted(false);
        }
        if (!originalEntry.isPosted()) {
            return ApiResponse.error("Only posted entries can be reversed", "NOT_POSTED");
        }
        if ("CANCELED".equals(originalEntry.getStatus()) || "REVERSED".equals(originalEntry.getStatus())) {
            return ApiResponse.error("Cannot reverse a canceled or reversed entry", "INVALID_STATUS");
        }

        // check if period is locked
        AccountingPeriod period = accountingPeriodService.findPeriodForDate(LocalDate.now());
        if (period == null) {
            return ApiResponse.error("No accounting period found for current date", "NO_PERIOD_FOUND");
        }
        if (period.isLocked()) {
            return ApiResponse.error("Cannot create reversal entry in a locked period", "PERIOD_LOCKED");
        }

        // generate entry number for reversal
        String entryNumber = generateEntryNumber(period);

        // create reversal entry
        JournalEntry reversalEntry = new JournalEntry();
        reversalEntry.setDescription("Reversal of entry #" + originalEntry.getEntryNumber());
        reversalEntry.setStatus("REVERSED");
        reversalEntry.setUserId(originalEntry.getUserId());
        reversalEntry.setPosted(false);
        reversalEntry.setCreatedAt(java.time.LocalDateTime.now());
        reversalEntry.setUpdatedAt(java.time.LocalDateTime.now());
        reversalEntry.setOriginalEntry(originalEntry);
        reversalEntry.setEntryNumber(entryNumber);

        // create reversed lines (swap debit and credit)
        List<JournalEntryLine> reversalLines = originalEntry.getLines().stream().map(line -> {
            JournalEntryLine reversalLine = new JournalEntryLine();
            reversalLine.setJournalEntry(reversalEntry);
            reversalLine.setAccountId(line.getAccountId());
            reversalLine.setDebit(line.getCredit() != null ? line.getCredit() : BigDecimal.ZERO);
            reversalLine.setCredit(line.getDebit() != null ? line.getDebit() : BigDecimal.ZERO);
            return reversalLine;
        }).collect(Collectors.toList());

        reversalEntry.setLines(reversalLines);
        entityManager.persist(reversalEntry);

        // log audit
        logAudit(reversalEntry.getId(), "REVERSED", originalEntry.getUserId(), 
                "Reversed journal entry: " + originalEntry.getEntryNumber() + " to new entry: " + entryNumber);

        JournalEntryDto responseDto = mapToDto(reversalEntry);
        return ApiResponse.success(responseDto, "Journal entry reversed successfully");
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

    @Transactional(readOnly = true)
    public ApiResponse<List<JournalEntryAuditDto>> getJournalEntryAuditTrail(Long journalEntryId) {
        JournalEntry entry = entityManager.find(JournalEntry.class, journalEntryId);
        if (entry == null) {
            return ApiResponse.error("Journal entry not found", "ENTRY_NOT_FOUND");
        }

        List<JournalEntryAudit> audits = entityManager.createQuery(
                "SELECT a FROM JournalEntryAudit a WHERE a.journalEntryId = :journalEntryId ORDER BY a.createdAt",
                JournalEntryAudit.class)
                .setParameter("journalEntryId", journalEntryId)
                .getResultList();

        List<JournalEntryAuditDto> auditDtos = audits.stream().map(this::mapToAuditDto).collect(Collectors.toList());
        return ApiResponse.success(auditDtos, "Audit trail retrieved successfully");
    }

    private void logAudit(Long journalEntryId, String action, Long userId, String details) {
        JournalEntryAudit audit = new JournalEntryAudit();
        audit.setJournalEntryId(journalEntryId);
        audit.setAction(action);
        audit.setUserId(userId);
        audit.setDetails(details);
        audit.setCreatedAt(java.time.LocalDateTime.now());
        entityManager.persist(audit);
    }

    private boolean isValidStatus(String status) {
        return status != null && List.of("CREATED", "VALIDATED", "CANCELED", "REVERSED").contains(status);
    }

    private String generateEntryNumber(AccountingPeriod period) {
        // find or create sequence for the period
        JournalEntrySequence sequence = entityManager.createQuery(
                "SELECT s FROM JournalEntrySequence s WHERE s.period.id = :periodId",
                JournalEntrySequence.class)
                .setParameter("periodId", period.getId())
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        if (sequence == null) {
            sequence = new JournalEntrySequence();
            sequence.setPeriod(period);
            sequence.setNextSequence(1);
            entityManager.persist(sequence);
        }

        int currentSequence = sequence.getNextSequence();
        sequence.setNextSequence(currentSequence + 1);
        sequence.setUpdatedAt(java.time.LocalDateTime.now());
        entityManager.merge(sequence);

        // format entry number: YYYY-MM-NNNN
        String yearMonth = period.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String sequenceNumber = String.format("%04d", currentSequence);
        return yearMonth + "-" + sequenceNumber;
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
        dto.setOriginalEntryId(entry.getOriginalEntry() != null ? entry.getOriginalEntry().getId() : null);
        dto.setEntryNumber(entry.getEntryNumber());
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

    private JournalEntryAuditDto mapToAuditDto(JournalEntryAudit audit) {
        JournalEntryAuditDto dto = new JournalEntryAuditDto();
        dto.setId(audit.getId());
        dto.setPrefix(audit.getPrefix());
        dto.setJournalEntryId(audit.getJournalEntryId());
        dto.setAction(audit.getAction());
        dto.setUserId(audit.getUserId());
        dto.setDetails(audit.getDetails());
        dto.setCreatedAt(audit.getCreatedAt());
        return dto;
    }
}