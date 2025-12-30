package com.example.controller;

import com.example.entity.Ticket;
import com.example.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // SENARYO: Spam koruması (Max 3 açık talep) burada tetiklenir
    // DİKKAT: Artık Resident ID'yi URL'den alıyoruz
    @PostMapping("/resident/{residentId}")
    public ResponseEntity<Ticket> createTicket(@PathVariable Long residentId, @RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.createTicket(residentId, ticket));
    }
    
    @PutMapping("/close/{id}")
    public ResponseEntity<Ticket> closeTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.closeTicket(id));
    }
}
