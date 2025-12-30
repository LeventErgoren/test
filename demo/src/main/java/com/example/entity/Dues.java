package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dues")
public class Dues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int month; // 1-12
    private int year;  // 2024

    private BigDecimal amount; // Borç tutarı

    @ManyToOne
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;
}
