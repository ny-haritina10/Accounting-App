package mg.module.accounting.services.account;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;
import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.ChartOfAccountDTO;
import mg.module.accounting.models.AccountType;
import mg.module.accounting.models.ChartOfAccount;
import mg.module.accounting.repositories.AccountTypeRepository;
import mg.module.accounting.repositories.ChartOfAccountRepository;

@Service
public class ChartOfAccountService {

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private ChartOfAccountRepository chartOfAccountRepository;

    @PostConstruct
    public void initializeDefaultAccounts() {
        // initialize account types if not present
        String[] accountTypes = {"ASSET", "LIABILITY", "EQUITY", "REVENUE", "EXPENSE"};
        for (String type : accountTypes) {
            if (!accountTypeRepository.existsByLabel(type)) {
                AccountType accountType = new AccountType();
                accountType.setLabel(type);
                accountTypeRepository.save(accountType);
            }
        }

        // initialize default chart of accounts
        List<AccountType> types = accountTypeRepository.findAll();
        if (chartOfAccountRepository.count() == 0) {
            createDefaultAccount("1000", "Cash", types.stream().filter(t -> t.getLabel().equals("ASSET")).findFirst().orElse(null));
            createDefaultAccount("2000", "Accounts Payable", types.stream().filter(t -> t.getLabel().equals("LIABILITY")).findFirst().orElse(null));
            createDefaultAccount("3000", "Owner's Equity", types.stream().filter(t -> t.getLabel().equals("EQUITY")).findFirst().orElse(null));
            createDefaultAccount("4000", "Sales Revenue", types.stream().filter(t -> t.getLabel().equals("REVENUE")).findFirst().orElse(null));
            createDefaultAccount("5000", "Operating Expenses", types.stream().filter(t -> t.getLabel().equals("EXPENSE")).findFirst().orElse(null));
        }
    }

    private void createDefaultAccount(String code, String name, AccountType type) {
        if (type != null) {
            ChartOfAccount account = new ChartOfAccount();
            account.setAccountCode(code);
            account.setAccountName(name);
            account.setAccountType(type);
            chartOfAccountRepository.save(account);
        }
    }

    public ApiResponse<ChartOfAccount> createAccount(ChartOfAccountDTO dto) {
        if (chartOfAccountRepository.existsByAccountCode(dto.getAccountCode())) {
            return ApiResponse.error("Account code already exists", "ACCOUNT_CODE_EXISTS");
        }

        AccountType accountType = accountTypeRepository.findById(dto.getAccountTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account type not found"));

        ChartOfAccount account = new ChartOfAccount();
        account.setAccountCode(dto.getAccountCode());
        account.setAccountName(dto.getAccountName());
        account.setAccountType(accountType);
        chartOfAccountRepository.save(account);

        return ApiResponse.success(account, "Account created successfully");
    }

    public ApiResponse<ChartOfAccount> updateAccount(Long id, ChartOfAccountDTO dto) {
        ChartOfAccount account = chartOfAccountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (!account.getAccountCode().equals(dto.getAccountCode()) && 
            chartOfAccountRepository.existsByAccountCode(dto.getAccountCode())) {
            return ApiResponse.error("Account code already exists", "ACCOUNT_CODE_EXISTS");
        }

        AccountType accountType = accountTypeRepository.findById(dto.getAccountTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account type not found"));

        account.setAccountCode(dto.getAccountCode());
        account.setAccountName(dto.getAccountName());
        account.setAccountType(accountType);
        account.setUpdatedAt(LocalDateTime.now());
        chartOfAccountRepository.save(account);

        return ApiResponse.success(account, "Account updated successfully");
    }

    public ApiResponse<Void> deleteAccount(Long id) {
        if (!chartOfAccountRepository.existsById(id)) {
            return ApiResponse.error("Account not found", "ACCOUNT_NOT_FOUND");
        }

        chartOfAccountRepository.deleteById(id);
        return ApiResponse.success(null, "Account deleted successfully");
    }

    public ApiResponse<List<ChartOfAccount>> getAllAccounts() {
        List<ChartOfAccount> accounts = chartOfAccountRepository.findAll();
        return ApiResponse.success(accounts, "Accounts retrieved successfully");
    }
}
