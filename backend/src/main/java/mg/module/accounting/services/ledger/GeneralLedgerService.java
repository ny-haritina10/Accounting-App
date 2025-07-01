
package mg.module.accounting.services.ledger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import mg.module.accounting.dto.GeneralLedgerEntryDTO;
import mg.module.accounting.models.LedgerEntry;
import mg.module.accounting.repositories.LedgerEntryRepository;

@Service
public class GeneralLedgerService {

    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;

    public List<GeneralLedgerEntryDTO> getGeneralLedger(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("accountName").and(Sort.by("transactionDate")));
        Page<LedgerEntry> ledgerPage = ledgerEntryRepository.findAll(pageable);
        
        return ledgerPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GeneralLedgerEntryDTO> searchLedgerEntries(
            String startDate, String endDate, String accountName, String accountNumber,
            String journalType, String narration, int page, int size) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = (startDate != null && !startDate.isEmpty()) ? LocalDateTime.parse(startDate, formatter) : null;
        LocalDateTime end = (endDate != null && !endDate.isEmpty()) ? LocalDateTime.parse(endDate, formatter) : null;

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

        Page<LedgerEntry> ledgerPage = ledgerEntryRepository.searchLedgerEntries(
                start, end, accountName, accountNumber, journalType, narration, pageable);

        return ledgerPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private GeneralLedgerEntryDTO convertToDTO(LedgerEntry entry) {
        return new GeneralLedgerEntryDTO(
            entry.getId(),
            entry.getAccountName(),
            entry.getTransactionDate(),
            entry.getDebit(),
            entry.getCredit(),
            entry.getBalance(),
            entry.getDescription()
        );
    }
}
