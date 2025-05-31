package mg.module.accounting.controllers.journal;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.JournalEntryAuditDto;
import mg.module.accounting.dto.JournalEntryDto;
import mg.module.accounting.services.journal.JournalEntryService;

@RestController
@RequestMapping("/api/journal-entries")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @PostMapping
    public ResponseEntity<ApiResponse<JournalEntryDto>> createJournalEntry(@RequestBody JournalEntryDto dto) {
        ApiResponse<JournalEntryDto> response = journalEntryService.createJournalEntry(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/post")
    public ResponseEntity<ApiResponse<JournalEntryDto>> postJournalEntry(@PathVariable Long id) {
        ApiResponse<JournalEntryDto> response = journalEntryService.postJournalEntry(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<ApiResponse<JournalEntryDto>> validateJournalEntry(@PathVariable Long id) {
        ApiResponse<JournalEntryDto> response = journalEntryService.validateJournalEntry(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<JournalEntryDto>> cancelJournalEntry(@PathVariable Long id) {
        ApiResponse<JournalEntryDto> response = journalEntryService.cancelJournalEntry(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reverse")
    public ResponseEntity<ApiResponse<JournalEntryDto>> reverseJournalEntry(@PathVariable Long id) {
        ApiResponse<JournalEntryDto> response = journalEntryService.reverseJournalEntry(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JournalEntryDto>>> getAllJournalEntries(
            @RequestParam(defaultValue = "false") boolean postedOnly) {
        ApiResponse<List<JournalEntryDto>> response = journalEntryService.getAllJournalEntries(postedOnly);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalEntryDto>> getJournalEntryById(@PathVariable Long id) {
        ApiResponse<JournalEntryDto> response = journalEntryService.getJournalEntryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/audit-trail")
    public ResponseEntity<ApiResponse<List<JournalEntryAuditDto>>> getJournalEntryAuditTrail(@PathVariable Long id) {
        ApiResponse<List<JournalEntryAuditDto>> response = journalEntryService.getJournalEntryAuditTrail(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<JournalEntryDto>>> searchJournalEntries(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(defaultValue = "false") boolean postedOnly) {
        ApiResponse<List<JournalEntryDto>> response = journalEntryService.searchJournalEntries(startDate, endDate, status, userId, accountId, postedOnly);
        return ResponseEntity.ok(response);
    }
}