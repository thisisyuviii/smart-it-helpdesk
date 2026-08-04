package com.helpdesk.sys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpdesk.sys.dto.request.TicketCreateRequest;
import com.helpdesk.sys.dto.response.TicketResponse;
import com.helpdesk.sys.entity.Priority;
import com.helpdesk.sys.entity.TicketStatus;
import com.helpdesk.sys.security.JwtTokenProvider;
import com.helpdesk.sys.security.UserPrincipal;
import com.helpdesk.sys.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username = "emp_alice", roles = {"EMPLOYEE"})
    void testCreateTicketEndpoint() throws Exception {
        TicketCreateRequest request = new TicketCreateRequest("Laptop Screen Flickering", "Screen goes black every 5 minutes", 1L, Priority.MEDIUM);

        TicketResponse mockResponse = new TicketResponse();
        mockResponse.setId(1L);
        mockResponse.setTitle("Laptop Screen Flickering");
        mockResponse.setDescription("Screen goes black every 5 minutes");
        mockResponse.setStatus(TicketStatus.CREATED);
        mockResponse.setPriority(Priority.MEDIUM);

        when(ticketService.createTicket(any(TicketCreateRequest.class), any(UserPrincipal.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Laptop Screen Flickering"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }
}
