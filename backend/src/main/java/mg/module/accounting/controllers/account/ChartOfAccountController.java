package mg.module.accounting.controllers.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.ChartOfAccountDTO;
import mg.module.accounting.models.ChartOfAccount;
import mg.module.accounting.services.account.ChartOfAccountService;

@RestController
@RequestMapping("/api/accounts")
public class ChartOfAccountController {

    @Autowired
    private ChartOfAccountService chartOfAccountService;

    @PostMapping
    public ApiResponse<ChartOfAccount> createAccount(@RequestBody ChartOfAccountDTO dto) {
        return chartOfAccountService.createAccount(dto);
    }

    @PutMapping("/{id}")
    public ApiResponse<ChartOfAccount> updateAccount(@PathVariable Long id, @RequestBody ChartOfAccountDTO dto) {
        return chartOfAccountService.updateAccount(id, dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAccount(@PathVariable Long id) {
        return chartOfAccountService.deleteAccount(id);
    }

    @GetMapping
    public ApiResponse<List<ChartOfAccount>> getAllAccounts() {
        return chartOfAccountService.getAllAccounts();
    }
}