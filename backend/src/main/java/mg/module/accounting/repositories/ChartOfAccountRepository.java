package mg.module.accounting.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.module.accounting.models.ChartOfAccount;

public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, Long> {
    boolean existsByAccountCode(String accountCode);
    Optional<ChartOfAccount> findByAccountCode(String accountCode);
}