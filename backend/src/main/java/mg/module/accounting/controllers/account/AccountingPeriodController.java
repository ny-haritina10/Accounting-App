package mg.module.accounting.controllers.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.AccountingPeriodDto;
import mg.module.accounting.services.account.AccountingPeriodService;

@RestController
@RequestMapping("/api/accounting-periods")
public class AccountingPeriodController {

    @Autowired
    private AccountingPeriodService accountingPeriodService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountingPeriodDto>> createAccountingPeriod(@RequestBody AccountingPeriodDto dto) {
        ApiResponse<AccountingPeriodDto> response = accountingPeriodService.createAccountingPeriod(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/lock")
    public ResponseEntity<ApiResponse<AccountingPeriodDto>> lockAccountingPeriod(@PathVariable Long id) {
        ApiResponse<AccountingPeriodDto> response = accountingPeriodService.lockAccountingPeriod(id);
        return ResponseEntity.ok(response);
    }
}