package mg.module.accounting.dto;

import java.time.LocalDateTime;
import java.util.List;

public class JournalEntryDto {

    private Long id;
    private String description;
    private String status;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<JournalEntryLineDto> lines;
    private Boolean posted;
    private Long originalEntryId;
    private String entryNumber;


    // Getters and setters
    public Boolean isPosted() {
        return posted;
    }

    public void setPosted(Boolean posted) {
        this.posted = posted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<JournalEntryLineDto> getLines() {
        return lines;
    }

    public void setLines(List<JournalEntryLineDto> lines) {
        this.lines = lines;
    }

    public Boolean getPosted() {
        return posted;
    }

    public Long getOriginalEntryId() {
        return originalEntryId;
    }

    public void setOriginalEntryId(Long originalEntryId) {
        this.originalEntryId = originalEntryId;
    }

    public String getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(String entryNumber) {
        this.entryNumber = entryNumber;
    }
}