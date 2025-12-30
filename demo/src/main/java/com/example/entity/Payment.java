package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount; // Ödenen miktar
    private LocalDate paymentDate;

    @ManyToOne
    @JoinColumn(name = "dues_id", nullable = false)
    private Dues dues;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Resident resident; // Ödemeyi yapan kişi
}
