package com.helpdesk.sys.controller;

import com.helpdesk.sys.dto.response.TicketReportResponse;
import com.helpdesk.sys.service.ReportService;
import com.helpdesk.sys.service.SlaSchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports & SLA Engine", description = "Endpoints for Executive Helpdesk Aggregates and Manual SLA Engine Trigger")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;
    private final SlaSchedulerService slaSchedulerService;

    public ReportController(ReportService reportService, SlaSchedulerService slaSchedulerService) {
        this.reportService = reportService;
        this.slaSchedulerService = slaSchedulerService;
    }

    @GetMapping("/metrics")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get aggregated ticket metrics computed via PL/SQL Stored Procedure sp_get_helpdesk_metrics()")
    public ResponseEntity<TicketReportResponse> getMetrics() {
        return ResponseEntity.ok(reportService.getHelpdeskMetrics());
    }

    @PostMapping("/trigger-sla-check")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Manually trigger the PL/SQL SLA Breach detection stored procedure sp_check_sla_breaches()")
    public ResponseEntity<String> triggerSlaCheck() {
        slaSchedulerService.checkSlaBreachesScheduled();
        return ResponseEntity.ok("PL/SQL SLA Breach detection stored procedure invoked successfully.");
    }
}
