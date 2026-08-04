package com.helpdesk.sys.dto.request;

import com.helpdesk.sys.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;

public class TicketStatusUpdateRequest {

    @NotNull(message = "New ticket status is required")
    private TicketStatus status;

    private Long assignedAgentId;

    private String comment;

    public TicketStatusUpdateRequest() {
    }

    public TicketStatusUpdateRequest(TicketStatus status, Long assignedAgentId, String comment) {
        this.status = status;
        this.assignedAgentId = assignedAgentId;
        this.comment = comment;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Long getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(Long assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
