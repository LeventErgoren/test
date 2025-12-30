package com.example.EntegrasyonTestleri;

import com.example.controller.TicketController;
import com.example.entity.Ticket;
import com.example.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
public class TicketControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TicketService ticketService;

    @Test
    public void getAllTicketsTest() throws Exception {
        Ticket ticket = new Ticket();
        when(ticketService.getAllTickets()).thenReturn(List.of(ticket, ticket));

        mockMvc.perform(get("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void createTicketTest() throws Exception {
        Long residentId = 1L;
        Ticket ticket = new Ticket();
        Ticket dbTicket = new Ticket();
        dbTicket.setId(10L);
        when(ticketService.createTicket(anyLong(), any(Ticket.class))).thenReturn(dbTicket);

        mockMvc.perform(post("/api/tickets/resident/{residentId}", residentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    public void closeTicketTest() throws Exception {
        Long ticketId = 5L;
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        when(ticketService.closeTicket(ticketId)).thenReturn(ticket);

        mockMvc.perform(put("/api/tickets/close/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId));
    }
}
