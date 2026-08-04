package com.helpdesk.sys.service;

import com.helpdesk.sys.dto.request.TicketCreateRequest;
import com.helpdesk.sys.dto.request.TicketOverrideAiRequest;
import com.helpdesk.sys.dto.request.TicketStatusUpdateRequest;
import com.helpdesk.sys.dto.response.TicketAuditLogResponse;
import com.helpdesk.sys.dto.response.TicketResponse;
import com.helpdesk.sys.security.UserPrincipal;

import java.util.List;

public interface TicketService {
    TicketResponse createTicket(TicketCreateRequest request, UserPrincipal currentUser);
    List<TicketResponse> getAllTickets();
    TicketResponse getTicketById(Long id);
    List<TicketResponse> getMyTickets(UserPrincipal currentUser);
    TicketResponse updateTicketStatus(Long ticketId, TicketStatusUpdateRequest request, UserPrincipal currentUser);
    TicketResponse overrideAiClassification(Long ticketId, TicketOverrideAiRequest request, UserPrincipal currentUser);
    List<TicketAuditLogResponse> getTicketAuditLogs(Long ticketId);
}
