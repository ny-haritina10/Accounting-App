package mg.module.accounting.controllers.ledger;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.GeneralLedgerEntryDTO;
import mg.module.accounting.services.ledger.GeneralLedgerService;

@RestController
@RequestMapping("/api/ledger")
public class GeneralLedgerController {

    @Autowired
    private GeneralLedgerService generalLedgerService;

    @GetMapping
    public ApiResponse<List<GeneralLedgerEntryDTO>> getGeneralLedger(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<GeneralLedgerEntryDTO> entries = generalLedgerService.getGeneralLedger(page, size);
        return ApiResponse.success(entries, "General ledger retrieved successfully");
    }

    @GetMapping("/search")
    public ApiResponse<List<GeneralLedgerEntryDTO>> searchLedgerEntries(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String journalType,
            @RequestParam(required = false) String narration,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<GeneralLedgerEntryDTO> entries = generalLedgerService.searchLedgerEntries(
                startDate, endDate, accountName, accountNumber, journalType, narration, page, size);
        return ApiResponse.success(entries, "Ledger entries retrieved successfully");
    }
}