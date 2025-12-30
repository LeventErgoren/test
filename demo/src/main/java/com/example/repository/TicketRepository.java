package com.example.repository;

import com.example.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // SENARYO: Spam Koruması -> Sakinin "OPEN" statüsündeki talep sayısı
    int countByResidentIdAndStatus(Long residentId, String status);

    List<Ticket> findByResidentId(Long residentId);
}
