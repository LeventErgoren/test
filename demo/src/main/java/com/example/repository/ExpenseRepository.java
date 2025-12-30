package com.example.repository;

import com.example.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    // Tarih aralığına göre gider raporu (Entegrasyon testi için)
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
}
