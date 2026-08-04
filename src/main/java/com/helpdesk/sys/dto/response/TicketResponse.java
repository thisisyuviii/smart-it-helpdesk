package com.helpdesk.sys.dto.response;

import com.helpdesk.sys.entity.Priority;
import com.helpdesk.sys.entity.Ticket;
import com.helpdesk.sys.entity.TicketStatus;

import java.time.LocalDateTime;

public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private Priority priority;
    private CategoryResponse category;
    private UserSummaryResponse createdBy;
    private UserSummaryResponse assignedAgent;
    private String aiSuggestedCategory;
    private String aiSuggestedPriority;
    private String aiSummary;
    private Boolean isAiOverridden;
    private Boolean isSlaBreached;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    public TicketResponse() {
    }

    public static TicketResponse fromEntity(Ticket ticket) {
        if (ticket == null) return null;

        TicketResponse resp = new TicketResponse();
        resp.setId(ticket.getId());
        resp.setTitle(ticket.getTitle());
        resp.setDescription(ticket.getDescription());
        resp.setStatus(ticket.getStatus());
        resp.setPriority(ticket.getPriority());
        resp.setCategory(CategoryResponse.fromEntity(ticket.getCategory()));
        resp.setCreatedBy(UserSummaryResponse.fromEntity(ticket.getCreatedBy()));
        resp.setAssignedAgent(UserSummaryResponse.fromEntity(ticket.getAssignedAgent()));
        resp.setAiSuggestedCategory(ticket.getAiSuggestedCategory());
        resp.setAiSuggestedPriority(ticket.getAiSuggestedPriority());
        resp.setAiSummary(ticket.getAiSummary());
        resp.setIsAiOverridden(ticket.getIsAiOverridden());
        resp.setIsSlaBreached(ticket.getIsSlaBreached());
        resp.setCreatedAt(ticket.getCreatedAt());
        resp.setUpdatedAt(ticket.getUpdatedAt());
        resp.setResolvedAt(ticket.getResolvedAt());
        resp.setClosedAt(ticket.getClosedAt());
        return resp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public UserSummaryResponse getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserSummaryResponse createdBy) {
        this.createdBy = createdBy;
    }

    public UserSummaryResponse getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(UserSummaryResponse assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    public String getAiSuggestedCategory() {
        return aiSuggestedCategory;
    }

    public void setAiSuggestedCategory(String aiSuggestedCategory) {
        this.aiSuggestedCategory = aiSuggestedCategory;
    }

    public String getAiSuggestedPriority() {
        return aiSuggestedPriority;
    }

    public void setAiSuggestedPriority(String aiSuggestedPriority) {
        this.aiSuggestedPriority = aiSuggestedPriority;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public Boolean getIsAiOverridden() {
        return isAiOverridden;
    }

    public void setIsAiOverridden(Boolean isAiOverridden) {
        this.isAiOverridden = isAiOverridden;
    }

    public Boolean getIsSlaBreached() {
        return isSlaBreached;
    }

    public void setIsSlaBreached(Boolean isSlaBreached) {
        this.isSlaBreached = isSlaBreached;
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

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}
