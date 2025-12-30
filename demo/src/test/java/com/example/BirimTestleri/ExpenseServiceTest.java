package com.example.BirimTestleri;

import com.example.entity.Dues;
import com.example.entity.Expense;
import com.example.entity.Flat;
import com.example.repository.DuesRepository;
import com.example.repository.ExpenseRepository;
import com.example.repository.FlatRepository;
import com.example.service.ExpenseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @Mock
    ExpenseRepository expenseRepository;

    @Mock
    FlatRepository flatRepository;

    @Mock
    DuesRepository duesRepository;

    @InjectMocks
    ExpenseService expenseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllExpensesTest() {
        Expense expense = new Expense();
        when(expenseRepository.findAll()).thenReturn(List.of(expense, expense));
        List<Expense> allExpenses = expenseService.getAllExpenses();

        Assertions.assertEquals(2, allExpenses.size());
    }

    @Test
    public void saveExpenseTest() {
        Expense expense = new Expense();
        Expense savedExpense = new Expense();
        savedExpense.setId(1L);
        when(expenseRepository.save(expense)).thenReturn(savedExpense);
        Expense dbExpense = expenseService.saveExpense(expense);

        Assertions.assertEquals(1L, dbExpense.getId());
    }

    @Test
    public void saveExpenseAmountNull_shouldOnlySaveExpense() {
        Expense expense = new Expense();
        expense.setAmount(null);

        Expense savedExpense = new Expense();
        savedExpense.setId(1L);
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        Expense dbExpense = expenseService.saveExpense(expense);

        Assertions.assertEquals(1L, dbExpense.getId());
        verifyNoInteractions(flatRepository);
        verify(duesRepository, never()).findByFlatIdAndMonthAndYear(anyLong(), anyInt(), anyInt());
        verify(duesRepository, never()).save(any(Dues.class));
    }

    @Test
    public void saveExpenseAmountZero_shouldOnlySaveExpense() {
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);

        Expense savedExpense = new Expense();
        savedExpense.setId(2L);
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        Expense dbExpense = expenseService.saveExpense(expense);

        Assertions.assertEquals(2L, dbExpense.getId());
        verifyNoInteractions(flatRepository);
        verify(duesRepository, never()).findByFlatIdAndMonthAndYear(anyLong(), anyInt(), anyInt());
        verify(duesRepository, never()).save(any(Dues.class));
    }

    @Test
    public void saveExpenseExpenseDateNull_shouldSetToTodayBeforeSave() {
        Expense expense = new Expense();
        expense.setExpenseDate(null);
        expense.setAmount(null); // dağıtım akışına girmesin, sadece tarihin setlenmesini test edelim

        ArgumentCaptor<Expense> expenseCaptor = ArgumentCaptor.forClass(Expense.class);

        Expense savedExpense = new Expense();
        savedExpense.setId(3L);
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        expenseService.saveExpense(expense);

        verify(expenseRepository).save(expenseCaptor.capture());
        Assertions.assertNotNull(expenseCaptor.getValue().getExpenseDate());
        Assertions.assertEquals(LocalDate.now(), expenseCaptor.getValue().getExpenseDate());
    }

    @Test
    public void saveExpenseAllFlatsEmpty_shouldNotCreateOrUpdateDues() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.00"));
        expense.setExpenseDate(LocalDate.of(2025, 1, 10));

        Expense savedExpense = new Expense();
        savedExpense.setId(4L);
        savedExpense.setAmount(expense.getAmount());
        savedExpense.setExpenseDate(expense.getExpenseDate());
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        Flat f1 = new Flat();
        f1.setId(1L);
        f1.setEmpty(true);
        Flat f2 = new Flat();
        f2.setId(2L);
        f2.setEmpty(true);
        when(flatRepository.findAll()).thenReturn(List.of(f1, f2));

        Expense result = expenseService.saveExpense(expense);

        Assertions.assertEquals(4L, result.getId());
        verify(duesRepository, never()).findByFlatIdAndMonthAndYear(anyLong(), anyInt(), anyInt());
        verify(duesRepository, never()).save(any(Dues.class));
    }

    @Test
    public void saveExpenseExistingDues_shouldIncreaseAmounts() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.00"));
        expense.setExpenseDate(LocalDate.of(2025, 1, 10));

        Expense savedExpense = new Expense();
        savedExpense.setId(5L);
        savedExpense.setAmount(expense.getAmount());
        savedExpense.setExpenseDate(expense.getExpenseDate());
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        Flat f1 = new Flat();
        f1.setId(11L);
        f1.setEmpty(false);
        Flat f2 = new Flat();
        f2.setId(22L);
        f2.setEmpty(false);
        when(flatRepository.findAll()).thenReturn(List.of(f1, f2));

        Dues d1 = Dues.builder().id(1L).flat(f1).month(1).year(2025).amount(new BigDecimal("10.00")).build();
        Dues d2 = Dues.builder().id(2L).flat(f2).month(1).year(2025).amount(new BigDecimal("0.00")).build();

        when(duesRepository.findByFlatIdAndMonthAndYear(eq(11L), eq(1), eq(2025))).thenReturn(Optional.of(d1));
        when(duesRepository.findByFlatIdAndMonthAndYear(eq(22L), eq(1), eq(2025))).thenReturn(Optional.of(d2));
        when(duesRepository.save(any(Dues.class))).thenAnswer(inv -> inv.getArgument(0));

        expenseService.saveExpense(expense);

        // 100 / 2 = 50, her daireye 50 eklenmeli
        Assertions.assertEquals(new BigDecimal("60.00"), d1.getAmount());
        Assertions.assertEquals(new BigDecimal("50.00"), d2.getAmount());
        verify(duesRepository, times(2)).save(any(Dues.class));
    }

    @Test
    public void saveExpenseDuesNotExists_shouldCreateAndSave() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("10.00"));
        expense.setExpenseDate(LocalDate.of(2025, 2, 1));

        Expense savedExpense = new Expense();
        savedExpense.setId(6L);
        savedExpense.setAmount(expense.getAmount());
        savedExpense.setExpenseDate(expense.getExpenseDate());
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        Flat f1 = new Flat();
        f1.setId(100L);
        f1.setEmpty(false);
        when(flatRepository.findAll()).thenReturn(List.of(f1));

        when(duesRepository.findByFlatIdAndMonthAndYear(eq(100L), eq(2), eq(2025))).thenReturn(Optional.empty());

        ArgumentCaptor<Dues> duesCaptor = ArgumentCaptor.forClass(Dues.class);
        when(duesRepository.save(any(Dues.class))).thenAnswer(inv -> inv.getArgument(0));

        expenseService.saveExpense(expense);

        verify(duesRepository).save(duesCaptor.capture());
        Dues savedDues = duesCaptor.getValue();
        Assertions.assertEquals(f1.getId(), savedDues.getFlat().getId());
        Assertions.assertEquals(2, savedDues.getMonth());
        Assertions.assertEquals(2025, savedDues.getYear());
        Assertions.assertEquals(new BigDecimal("10.00"), savedDues.getAmount());
    }

    @Test
    public void saveExpenseRemainderCents_shouldDistributeFairly() {
        // 100.01 / 2 => 50.00 + 50.01 (1 kuruş artığı ilk daireye)
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.01"));
        expense.setExpenseDate(LocalDate.of(2025, 3, 15));

        Expense savedExpense = new Expense();
        savedExpense.setId(7L);
        savedExpense.setAmount(expense.getAmount());
        savedExpense.setExpenseDate(expense.getExpenseDate());
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        Flat f1 = new Flat();
        f1.setId(1L);
        f1.setEmpty(false);
        Flat f2 = new Flat();
        f2.setId(2L);
        f2.setEmpty(false);
        when(flatRepository.findAll()).thenReturn(List.of(f1, f2));

        Dues d1 = Dues.builder().id(1L).flat(f1).month(3).year(2025).amount(BigDecimal.ZERO).build();
        Dues d2 = Dues.builder().id(2L).flat(f2).month(3).year(2025).amount(BigDecimal.ZERO).build();

        when(duesRepository.findByFlatIdAndMonthAndYear(eq(1L), eq(3), eq(2025))).thenReturn(Optional.of(d1));
        when(duesRepository.findByFlatIdAndMonthAndYear(eq(2L), eq(3), eq(2025))).thenReturn(Optional.of(d2));
        when(duesRepository.save(any(Dues.class))).thenAnswer(inv -> inv.getArgument(0));

        expenseService.saveExpense(expense);

        Assertions.assertEquals(new BigDecimal("50.01"), d1.getAmount());
        Assertions.assertEquals(new BigDecimal("50.00"), d2.getAmount());
    }

    @Test
    public void saveExpenseFlatsListContainsNull_shouldIgnoreNullAndChargeNonEmpty() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("10.00"));
        expense.setExpenseDate(LocalDate.of(2025, 4, 1));

        Expense savedExpense = new Expense();
        savedExpense.setId(8L);
        savedExpense.setAmount(expense.getAmount());
        savedExpense.setExpenseDate(expense.getExpenseDate());
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        Flat charged = new Flat();
        charged.setId(1L);
        charged.setEmpty(false);

        // List.of(null, charged) -> Java'da NPE atar. Null içeren liste için Arrays.asList kullanmalıyız.
        when(flatRepository.findAll()).thenReturn(Arrays.asList(null, charged));

        Dues dues = Dues.builder().id(1L).flat(charged).month(4).year(2025).amount(BigDecimal.ZERO).build();
        when(duesRepository.findByFlatIdAndMonthAndYear(eq(1L), eq(4), eq(2025))).thenReturn(Optional.of(dues));
        when(duesRepository.save(any(Dues.class))).thenAnswer(inv -> inv.getArgument(0));

        expenseService.saveExpense(expense);

        Assertions.assertEquals(new BigDecimal("10.00"), dues.getAmount());
    }

    @Test
    public void saveExpenseDuesAmountNull_shouldTreatAsZero() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("1.00"));
        expense.setExpenseDate(LocalDate.of(2025, 6, 1));

        Expense savedExpense = new Expense();
        savedExpense.setId(10L);
        savedExpense.setAmount(expense.getAmount());
        savedExpense.setExpenseDate(expense.getExpenseDate());
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        Flat f1 = new Flat();
        f1.setId(1L);
        f1.setEmpty(false);
        when(flatRepository.findAll()).thenReturn(List.of(f1));

        Dues dues = Dues.builder().id(1L).flat(f1).month(6).year(2025).amount(null).build();
        when(duesRepository.findByFlatIdAndMonthAndYear(eq(1L), eq(6), eq(2025))).thenReturn(Optional.of(dues));
        when(duesRepository.save(any(Dues.class))).thenAnswer(inv -> inv.getArgument(0));

        expenseService.saveExpense(expense);

        Assertions.assertEquals(new BigDecimal("1.00"), dues.getAmount());
    }
}
