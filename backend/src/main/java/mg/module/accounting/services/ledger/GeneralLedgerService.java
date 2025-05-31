package mg.module.accounting.services.ledger;

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