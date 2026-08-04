package com.helpdesk.sys.dto.response;

public class TicketReportResponse {
    private long totalTickets;
    private long totalCreated;
    private long totalOpen;
    private long totalInProgress;
    private long totalResolved;
    private long totalClosed;
    private long totalSlaBreached;
    private long totalAiOverridden;

    public TicketReportResponse() {
    }

    public TicketReportResponse(long totalTickets, long totalCreated, long totalOpen, long totalInProgress, long totalResolved, long totalClosed, long totalSlaBreached, long totalAiOverridden) {
        this.totalTickets = totalTickets;
        this.totalCreated = totalCreated;
        this.totalOpen = totalOpen;
        this.totalInProgress = totalInProgress;
        this.totalResolved = totalResolved;
        this.totalClosed = totalClosed;
        this.totalSlaBreached = totalSlaBreached;
        this.totalAiOverridden = totalAiOverridden;
    }

    public long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public long getTotalCreated() {
        return totalCreated;
    }

    public void setTotalCreated(long totalCreated) {
        this.totalCreated = totalCreated;
    }

    public long getTotalOpen() {
        return totalOpen;
    }

    public void setTotalOpen(long totalOpen) {
        this.totalOpen = totalOpen;
    }

    public long getTotalInProgress() {
        return totalInProgress;
    }

    public void setTotalInProgress(long totalInProgress) {
        this.totalInProgress = totalInProgress;
    }

    public long getTotalResolved() {
        return totalResolved;
    }

    public void setTotalResolved(long totalResolved) {
        this.totalResolved = totalResolved;
    }

    public long getTotalClosed() {
        return totalClosed;
    }

    public void setTotalClosed(long totalClosed) {
        this.totalClosed = totalClosed;
    }

    public long getTotalSlaBreached() {
        return totalSlaBreached;
    }

    public void setTotalSlaBreached(long totalSlaBreached) {
        this.totalSlaBreached = totalSlaBreached;
    }

    public long getTotalAiOverridden() {
        return totalAiOverridden;
    }

    public void setTotalAiOverridden(long totalAiOverridden) {
        this.totalAiOverridden = totalAiOverridden;
    }
}
