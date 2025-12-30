package com.example.BirimTestleri;

import com.example.entity.Resident;
import com.example.entity.Ticket;
import com.example.repository.ResidentRepository;
import com.example.repository.TicketRepository;
import com.example.service.TicketService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class TicketServiceTest {

    @Mock
    TicketRepository ticketRepository;

    @Mock
    ResidentRepository residentRepository;

    @InjectMocks
    TicketService ticketService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllTicketsTest() {
        Ticket ticket = new Ticket();
        when(ticketRepository.findAll()).thenReturn(List.of(ticket, ticket));
        List<Ticket> allTickets = ticketService.getAllTickets();

        Assertions.assertEquals(2, allTickets.size());
    }

    @Test
    public void closeTicketNotFoundException() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> ticketService.closeTicket(1L));
    }

    @Test
    public void closeTicketTest() {
        Ticket ticket = new Ticket();
        Ticket savedTicket = new Ticket();
        savedTicket.setStatus("CLOSED");
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(savedTicket);

        Ticket dbTicket = ticketService.closeTicket(1L);
        Assertions.assertEquals("CLOSED", dbTicket.getStatus());
    }

    @Test
    public void createTicketResidentNotFoundException() {
        Ticket ticket = new Ticket();
        when(residentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> ticketService.createTicket(1L, ticket));
        Assertions.assertEquals("Sakin bulunamadı", runtimeException.getMessage());
    }

    @Test
    public void createTicketTicketCountException() {
        Ticket ticket = new Ticket();
        Resident resident = new Resident();

        when(residentRepository.findById(1L)).thenReturn(Optional.of(resident));
        when(ticketRepository.countByResidentIdAndStatus(1L, "OPEN")).thenReturn(3);

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> ticketService.createTicket(1L, ticket));
        Assertions.assertEquals("Çok fazla açık talebiniz var. Lütfen önce mevcutların çözülmesini bekleyin.", runtimeException.getMessage());
    }

    @Test
    public void createTicketTest() {
        Ticket ticket = new Ticket();
        Resident resident = new Resident();
        Ticket dbTicket = new Ticket();
        dbTicket.setStatus("OPEN");

        when(residentRepository.findById(1L)).thenReturn(Optional.of(resident));
        when(ticketRepository.countByResidentIdAndStatus(1L, "OPEN")).thenReturn(1);
        when(ticketRepository.save(ticket)).thenReturn(dbTicket);

        Ticket serviceTicket = ticketService.createTicket(1L, ticket);
        Assertions.assertEquals("OPEN", serviceTicket.getStatus());
    }
}
