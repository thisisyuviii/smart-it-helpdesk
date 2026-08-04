package com.helpdesk.sys.dto.response;

import com.helpdesk.sys.entity.TicketAuditLog;
import com.helpdesk.sys.entity.TicketStatus;

import java.time.LocalDateTime;

public class TicketAuditLogResponse {
    private Long id;
    private Long ticketId;
    private TicketStatus oldStatus;
    private TicketStatus newStatus;
    private UserSummaryResponse changedBy;
    private String comment;
    private LocalDateTime changedAt;

    public TicketAuditLogResponse() {
    }

    public static TicketAuditLogResponse fromEntity(TicketAuditLog log) {
        if (log == null) return null;
        TicketAuditLogResponse resp = new TicketAuditLogResponse();
        resp.setId(log.getId());
        resp.setTicketId(log.getTicket().getId());
        resp.setOldStatus(log.getOldStatus());
        resp.setNewStatus(log.getNewStatus());
        resp.setChangedBy(UserSummaryResponse.fromEntity(log.getChangedBy()));
        resp.setComment(log.getComment());
        resp.setChangedAt(log.getChangedAt());
        return resp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public TicketStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(TicketStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public TicketStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(TicketStatus newStatus) {
        this.newStatus = newStatus;
    }

    public UserSummaryResponse getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(UserSummaryResponse changedBy) {
        this.changedBy = changedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
