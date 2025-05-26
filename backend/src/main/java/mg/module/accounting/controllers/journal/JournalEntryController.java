package mg.module.accounting.controllers.journal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.JournalEntryDto;
import mg.module.accounting.models.JournalEntry;
import mg.module.accounting.services.journal.JournalEntryService;

@RestController
@RequestMapping("/api/journal-entries")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @PostMapping
    public ResponseEntity<ApiResponse<JournalEntry>> createJournalEntry(@RequestBody JournalEntryDto dto) {
        ApiResponse<JournalEntry> response = journalEntryService.createJournalEntry(dto);
        return ResponseEntity.ok(response);
    }
}
