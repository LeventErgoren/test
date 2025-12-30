package com.example.service;

import com.example.entity.Ticket;
import com.example.entity.Resident;
import com.example.repository.TicketRepository;
import com.example.repository.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ResidentRepository residentRepository;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket createTicket(Long residentId, Ticket ticket) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new RuntimeException("Sakin bulunamadı"));

        // SENARYO 7 (ZOR): Spam Koruması
        // Açık statüde (OPEN) 3'ten fazla talebi varsa yenisini açtırma.
        int openTicketCount = ticketRepository.countByResidentIdAndStatus(residentId, "OPEN");

        if (openTicketCount >= 3) {
            throw new RuntimeException("Çok fazla açık talebiniz var. Lütfen önce mevcutların çözülmesini bekleyin.");
        }

        ticket.setStatus("OPEN");
        ticket.setResident(resident);
        return ticketRepository.save(ticket);
    }

    public Ticket closeTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Böyle bir ticket bulunamadı."));
        ticket.setStatus("CLOSED");
        return ticketRepository.save(ticket);
    }
}
