package com.helpdesk.sys.service;

import com.helpdesk.sys.dto.request.TicketCreateRequest;
import com.helpdesk.sys.dto.request.TicketStatusUpdateRequest;
import com.helpdesk.sys.dto.response.TicketResponse;
import com.helpdesk.sys.entity.*;
import com.helpdesk.sys.exception.InvalidTicketStatusTransitionException;
import com.helpdesk.sys.repository.CategoryRepository;
import com.helpdesk.sys.repository.TicketAuditLogRepository;
import com.helpdesk.sys.repository.TicketRepository;
import com.helpdesk.sys.repository.UserRepository;
import com.helpdesk.sys.security.UserPrincipal;
import com.helpdesk.sys.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TicketAuditLogRepository auditLogRepository;

    @Mock
    private AiTriageService aiTriageService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private User sampleUser;
    private UserPrincipal userPrincipal;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "john_doe", "john@example.com", "password", "John Doe", Role.EMPLOYEE, true, LocalDateTime.now());
        userPrincipal = new UserPrincipal(1L, "john_doe", "john@example.com", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE")), true);
        sampleCategory = new Category(1L, "HARDWARE", "Hardware related issues");
    }

    @Test
    @DisplayName("Should successfully create a ticket and trigger Async AI Triage")
    void testCreateTicket_Success() {
        TicketCreateRequest request = new TicketCreateRequest("VPN Connection Failed", "Cannot connect to corporate VPN from home", 1L, Priority.HIGH);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));

        Ticket savedTicket = new Ticket();
        savedTicket.setId(100L);
        savedTicket.setTitle(request.getTitle());
        savedTicket.setDescription(request.getDescription());
        savedTicket.setStatus(TicketStatus.CREATED);
        savedTicket.setPriority(Priority.HIGH);
        savedTicket.setCategory(sampleCategory);
        savedTicket.setCreatedBy(sampleUser);

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        TicketResponse response = ticketService.createTicket(request, userPrincipal);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("VPN Connection Failed", response.getTitle());
        assertEquals(TicketStatus.CREATED, response.getStatus());

        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(auditLogRepository, times(1)).save(any(TicketAuditLog.class));
        verify(aiTriageService, times(1)).analyzeTicketAsync(eq(100L), eq("VPN Connection Failed"), anyString());
    }

    @Test
    @DisplayName("Should throw exception when attempting invalid status transition from CLOSED to OPEN")
    void testUpdateTicketStatus_InvalidTransition() {
        Ticket closedTicket = new Ticket();
        closedTicket.setId(100L);
        closedTicket.setStatus(TicketStatus.CLOSED);
        closedTicket.setCreatedBy(sampleUser);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(closedTicket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        TicketStatusUpdateRequest updateRequest = new TicketStatusUpdateRequest(TicketStatus.OPEN, null, "Attempt reopen");

        assertThrows(InvalidTicketStatusTransitionException.class, () -> {
            ticketService.updateTicketStatus(100L, updateRequest, userPrincipal);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}
