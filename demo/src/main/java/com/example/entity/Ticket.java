package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // "Musluk damlatıyor"
    private String description;
    
    // Status: OPEN, IN_PROGRESS, CLOSED (String olarak tutuyoruz basit olsun diye)
    private String status; 
    
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;
}
