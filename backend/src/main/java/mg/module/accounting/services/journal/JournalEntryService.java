package mg.module.accounting.services.journal;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.JournalEntryDto;
import mg.module.accounting.models.JournalEntry;
import mg.module.accounting.models.JournalEntryLine;

@Service
public class JournalEntryService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ApiResponse<JournalEntry> createJournalEntry(JournalEntryDto dto) {
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

        return ApiResponse.success(entry, "Journal entry created successfully");
    }

    private boolean isValidStatus(String status) {
        return status != null && List.of("CREATED", "VALIDATED", "CANCELED", "REVERSED").contains(status);
    }
}