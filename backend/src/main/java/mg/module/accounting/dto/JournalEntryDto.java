package mg.module.accounting.dto;

import java.util.List;

public class JournalEntryDto {

    private String description;
    private String status;
    private Long userId;
    private List<JournalEntryLineDto> lines;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<JournalEntryLineDto> getLines() {
        return lines;
    }

    public void setLines(List<JournalEntryLineDto> lines) {
        this.lines = lines;
    }
}