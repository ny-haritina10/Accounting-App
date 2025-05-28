package mg.module.accounting.services.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.AccountingPeriodDto;
import mg.module.accounting.models.AccountingPeriod;

@Service
public class AccountingPeriodService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ApiResponse<AccountingPeriodDto> createAccountingPeriod(AccountingPeriodDto dto) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            return ApiResponse.error("Start date and end date are required", "INVALID_DATES");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            return ApiResponse.error("End date must be after start date", "INVALID_DATE_RANGE");
        }

        AccountingPeriod period = new AccountingPeriod();
        period.setStartDate(dto.getStartDate());
        period.setEndDate(dto.getEndDate());
        period.setLocked(false);
        entityManager.persist(period);

        AccountingPeriodDto responseDto = mapToDto(period);
        return ApiResponse.success(responseDto, "Accounting period created successfully");
    }

    @Transactional
    public ApiResponse<AccountingPeriodDto> lockAccountingPeriod(Long id) {
        AccountingPeriod period = entityManager.find(AccountingPeriod.class, id);
        if (period == null) {
            return ApiResponse.error("Accounting period not found", "PERIOD_NOT_FOUND");
        }
        if (period.isLocked()) {
            return ApiResponse.error("Accounting period already locked", "PERIOD_ALREADY_LOCKED");
        }

        period.setLocked(true);
        period.setUpdatedAt(java.time.LocalDateTime.now());
        entityManager.merge(period);

        AccountingPeriodDto responseDto = mapToDto(period);
        return ApiResponse.success(responseDto, "Accounting period locked successfully");
    }

    @Transactional(readOnly = true)
    public AccountingPeriod findPeriodForDate(java.time.LocalDate date) {
        return entityManager.createQuery(
                "SELECT p FROM AccountingPeriod p WHERE :date BETWEEN p.startDate AND p.endDate",
                AccountingPeriod.class)
                .setParameter("date", date)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    private AccountingPeriodDto mapToDto(AccountingPeriod period) {
        AccountingPeriodDto dto = new AccountingPeriodDto();
        dto.setId(period.getId());
        dto.setStartDate(period.getStartDate());
        dto.setEndDate(period.getEndDate());
        dto.setLocked(period.isLocked());
        dto.setCreatedAt(period.getCreatedAt());
        dto.setUpdatedAt(period.getUpdatedAt());
        return dto;
    }
}