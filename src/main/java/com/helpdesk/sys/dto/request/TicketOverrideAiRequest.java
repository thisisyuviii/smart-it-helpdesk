package com.helpdesk.sys.dto.request;

import com.helpdesk.sys.entity.Priority;
import jakarta.validation.constraints.NotNull;

public class TicketOverrideAiRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Priority is required")
    private Priority priority;

    public TicketOverrideAiRequest() {
    }

    public TicketOverrideAiRequest(Long categoryId, Priority priority) {
        this.categoryId = categoryId;
        this.priority = priority;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
