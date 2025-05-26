package mg.module.accounting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.module.accounting.models.AccountType;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
    boolean existsByLabel(String label);
}