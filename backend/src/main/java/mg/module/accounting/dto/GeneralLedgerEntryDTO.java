package mg.module.accounting.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GeneralLedgerEntryDTO {

    private Long id;
    private String accountName;
    private LocalDateTime transactionDate;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal balance;
    private String description;

    // constructor
    public GeneralLedgerEntryDTO(Long id, String accountName,
                                 LocalDateTime transactionDate, BigDecimal debit,
                                 BigDecimal credit, BigDecimal balance, String description) {
        this.id = id;
        this.accountName = accountName;
        this.transactionDate = transactionDate;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
        this.description = description;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = debit;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}