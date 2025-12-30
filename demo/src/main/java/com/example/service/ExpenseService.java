package com.example.service;

import com.example.entity.Dues;
import com.example.entity.Expense;
import com.example.entity.Flat;
import com.example.repository.DuesRepository;
import com.example.repository.ExpenseRepository;
import com.example.repository.FlatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final FlatRepository flatRepository;
    private final DuesRepository duesRepository;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Transactional
    public Expense saveExpense(Expense expense) {
        if (expense.getExpenseDate() == null) {
            expense.setExpenseDate(LocalDate.now());
        }
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return expenseRepository.save(expense);
        }

        Expense saved = expenseRepository.save(expense);

        int month = saved.getExpenseDate().getMonthValue();
        int year = saved.getExpenseDate().getYear();

        List<Flat> flats = flatRepository.findAll();
        List<Flat> chargedFlats = flats.stream().filter((f) -> f != null && !f.isEmpty()).toList();
        if (chargedFlats.isEmpty()) {
            return saved;
        }

        BigDecimal total = saved.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal count = new BigDecimal(chargedFlats.size());
        BigDecimal baseShare = total.divide(count, 2, RoundingMode.DOWN);
        BigDecimal remainder = total.subtract(baseShare.multiply(count));

        int remainderCents = remainder.movePointRight(2).intValue();
        BigDecimal oneCent = new BigDecimal("0.01");

        for (int i = 0; i < chargedFlats.size(); i++) {
            Flat flat = chargedFlats.get(i);
            BigDecimal add = baseShare.add(i < remainderCents ? oneCent : BigDecimal.ZERO);

            Dues dues = duesRepository.findByFlatIdAndMonthAndYear(flat.getId(), month, year)
                    .orElseGet(() -> Dues.builder().flat(flat).month(month).year(year).amount(BigDecimal.ZERO).build());

            BigDecimal current = dues.getAmount() == null ? BigDecimal.ZERO : dues.getAmount();
            dues.setAmount(current.add(add));
            duesRepository.save(dues);
        }

        return saved;
    }
}
