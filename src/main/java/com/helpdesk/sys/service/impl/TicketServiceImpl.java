package com.helpdesk.sys.service.impl;

import com.helpdesk.sys.dto.request.TicketCreateRequest;
import com.helpdesk.sys.dto.request.TicketOverrideAiRequest;
import com.helpdesk.sys.dto.request.TicketStatusUpdateRequest;
import com.helpdesk.sys.dto.response.TicketAuditLogResponse;
import com.helpdesk.sys.dto.response.TicketResponse;
import com.helpdesk.sys.entity.*;
import com.helpdesk.sys.exception.InvalidTicketStatusTransitionException;
import com.helpdesk.sys.exception.ResourceNotFoundException;
import com.helpdesk.sys.exception.UnauthorizedAccessException;
import com.helpdesk.sys.repository.CategoryRepository;
import com.helpdesk.sys.repository.TicketAuditLogRepository;
import com.helpdesk.sys.repository.TicketRepository;
import com.helpdesk.sys.repository.UserRepository;
import com.helpdesk.sys.security.UserPrincipal;
import com.helpdesk.sys.service.AiTriageService;
import com.helpdesk.sys.service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TicketAuditLogRepository auditLogRepository;
    private final AiTriageService aiTriageService;

    public TicketServiceImpl(TicketRepository ticketRepository,
                             UserRepository userRepository,
                             CategoryRepository categoryRepository,
                             TicketAuditLogRepository auditLogRepository,
                             AiTriageService aiTriageService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.auditLogRepository = auditLogRepository;
        this.aiTriageService = aiTriageService;
    }

    @Override
    @Transactional
    public TicketResponse createTicket(TicketCreateRequest request, UserPrincipal currentUser) {
        User creator = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(TicketStatus.CREATED);
        ticket.setCreatedBy(creator);
        ticket.setPriority(request.getPriority() != null ? request.getPriority() : Priority.LOW);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            ticket.setCategory(category);
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        // Record initial audit log
        TicketAuditLog auditLog = new TicketAuditLog(savedTicket, null, TicketStatus.CREATED, creator, "Ticket created");
        auditLogRepository.save(auditLog);

        // Trigger Async Gen AI Triage non-blockingly
        aiTriageService.analyzeTicketAsync(savedTicket.getId(), savedTicket.getTitle(), savedTicket.getDescription());

        return TicketResponse.fromEntity(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(TicketResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
        return TicketResponse.fromEntity(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getMyTickets(UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        if (user.getRole() == Role.AGENT) {
            return ticketRepository.findByAssignedAgentId(user.getId()).stream()
                    .map(TicketResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        return ticketRepository.findByCreatedById(user.getId()).stream()
                .map(TicketResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TicketResponse updateTicketStatus(Long ticketId, TicketStatusUpdateRequest request, UserPrincipal currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        User actingUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        // Authorization check: Agents & Admins can update status; Employee can only update if they own the ticket and closing it
        if (actingUser.getRole() == Role.EMPLOYEE && !ticket.getCreatedBy().getId().equals(actingUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to update this ticket.");
        }

        TicketStatus oldStatus = ticket.getStatus();
        TicketStatus newStatus = request.getStatus();

        // Validate state transitions
        validateStatusTransition(oldStatus, newStatus);

        ticket.setStatus(newStatus);

        // Handle agent assignment if specified
        if (request.getAssignedAgentId() != null) {
            User agent = userRepository.findById(request.getAssignedAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent", "id", request.getAssignedAgentId()));
            if (agent.getRole() != Role.AGENT && agent.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("Assigned user must have AGENT or ADMIN role.");
            }
            ticket.setAssignedAgent(agent);
        } else if (actingUser.getRole() == Role.AGENT && ticket.getAssignedAgent() == null) {
            // Auto-assign to acting agent if unassigned
            ticket.setAssignedAgent(actingUser);
        }

        // Handle timestamps
        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        Ticket updated = ticketRepository.save(ticket);

        // Audit Log
        String logComment = request.getComment() != null ? request.getComment() : "Status updated to " + newStatus;
        TicketAuditLog log = new TicketAuditLog(updated, oldStatus, newStatus, actingUser, logComment);
        auditLogRepository.save(log);

        return TicketResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public TicketResponse overrideAiClassification(Long ticketId, TicketOverrideAiRequest request, UserPrincipal currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        ticket.setCategory(category);
        ticket.setPriority(request.getPriority());
        ticket.setIsAiOverridden(true);

        Ticket updated = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketAuditLogResponse> getTicketAuditLogs(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket", "id", ticketId);
        }
        return auditLogRepository.findByTicketIdOrderByChangedAtDesc(ticketId).stream()
                .map(TicketAuditLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateStatusTransition(TicketStatus oldStatus, TicketStatus newStatus) {
        if (oldStatus == newStatus) return;

        if (oldStatus == TicketStatus.CLOSED) {
            throw new InvalidTicketStatusTransitionException("Cannot change status of a CLOSED ticket.");
        }

        if (oldStatus == TicketStatus.CREATED && (newStatus != TicketStatus.OPEN && newStatus != TicketStatus.IN_PROGRESS)) {
            throw new InvalidTicketStatusTransitionException("CREATED ticket can only transition to OPEN or IN_PROGRESS.");
        }

        if (oldStatus == TicketStatus.OPEN && (newStatus != TicketStatus.IN_PROGRESS && newStatus != TicketStatus.RESOLVED)) {
            throw new InvalidTicketStatusTransitionException("OPEN ticket can only transition to IN_PROGRESS or RESOLVED.");
        }

        if (oldStatus == TicketStatus.IN_PROGRESS && (newStatus != TicketStatus.RESOLVED && newStatus != TicketStatus.OPEN)) {
            throw new InvalidTicketStatusTransitionException("IN_PROGRESS ticket can only transition to RESOLVED or back to OPEN.");
        }

        if (oldStatus == TicketStatus.RESOLVED && (newStatus != TicketStatus.CLOSED && newStatus != TicketStatus.OPEN)) {
            throw new InvalidTicketStatusTransitionException("RESOLVED ticket can only transition to CLOSED or reopened to OPEN.");
        }
    }
}
