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
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description; // Elektrik Faturası, Asansör Bakımı
    private BigDecimal amount;
    private LocalDate expenseDate;

    // Gider tipi veya kategorisi (Opsiyonel string olarak tutuyoruz)
    private String category; 
}
