// File: src/main/java/mg/module/accounting/repositories/LedgerEntryRepository.java
package mg.module.accounting.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.module.accounting.models.LedgerEntry;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    Optional<LedgerEntry> findTopByAccountNameOrderByTransactionDateDesc(String accountName);
}