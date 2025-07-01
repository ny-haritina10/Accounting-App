
// File: src/main/java/mg/module/accounting/repositories/LedgerEntryRepository.java
package mg.module.accounting.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.module.accounting.models.LedgerEntry;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    Optional<LedgerEntry> findTopByAccountNameOrderByTransactionDateDesc(String accountName);

    @Query("SELECT le FROM LedgerEntry le " +
           "JOIN le.journalEntryLine jel " +
           "JOIN jel.journalEntry je " +
           "JOIN ChartOfAccount coa ON coa.id = jel.accountId " +
           "WHERE (:startDate IS NULL OR le.transactionDate >= :startDate) " +
           "AND (:endDate IS NULL OR le.transactionDate <= :endDate) " +
           "AND (:accountName IS NULL OR le.accountName LIKE %:accountName%) " +
           "AND (:accountNumber IS NULL OR coa.accountCode = :accountNumber) " +
           "AND (:journalType IS NULL OR je.prefix = :journalType) " +
           "AND (:narration IS NULL OR le.description LIKE %:narration%)")
    Page<LedgerEntry> searchLedgerEntries(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          @Param("accountName") String accountName,
                                          @Param("accountNumber") String accountNumber,
                                          @Param("journalType") String journalType,
                                          @Param("narration") String narration,
                                          Pageable pageable);
}
