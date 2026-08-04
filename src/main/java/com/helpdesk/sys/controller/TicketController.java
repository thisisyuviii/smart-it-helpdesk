package com.helpdesk.sys.controller;

import com.helpdesk.sys.dto.request.TicketCreateRequest;
import com.helpdesk.sys.dto.request.TicketOverrideAiRequest;
import com.helpdesk.sys.dto.request.TicketStatusUpdateRequest;
import com.helpdesk.sys.dto.response.TicketAuditLogResponse;
import com.helpdesk.sys.dto.response.TicketResponse;
import com.helpdesk.sys.security.UserPrincipal;
import com.helpdesk.sys.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Endpoints for Ticket Management, AI Triage, & Status Transitions")
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'AGENT', 'ADMIN')")
    @Operation(summary = "Create a new ticket and automatically trigger Gen AI Triage asynchronously")
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        TicketResponse response = ticketService.createTicket(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get all tickets (Agents and Admins only)")
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'AGENT', 'ADMIN')")
    @Operation(summary = "Get tickets associated with current logged-in user")
    public ResponseEntity<List<TicketResponse>> getMyTickets(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ticketService.getMyTickets(currentUser));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'AGENT', 'ADMIN')")
    @Operation(summary = "Get ticket details by ID")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Update ticket status (CREATED -> OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED) with audit trail")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long id,
            @Valid @RequestBody TicketStatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, request, currentUser));
    }

    @PutMapping("/{id}/override-ai")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Override AI-suggested category or priority by human agent")
    public ResponseEntity<TicketResponse> overrideAiClassification(
            @PathVariable Long id,
            @Valid @RequestBody TicketOverrideAiRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ticketService.overrideAiClassification(id, request, currentUser));
    }

    @GetMapping("/{id}/audit-logs")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get audit trail history of status changes for a ticket")
    public ResponseEntity<List<TicketAuditLogResponse>> getTicketAuditLogs(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketAuditLogs(id));
    }
}
