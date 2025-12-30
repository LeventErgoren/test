package com.example.BirimTestleri;

import com.example.entity.Dues;
import com.example.entity.Payment;
import com.example.repository.DuesRepository;
import com.example.repository.PaymentRepository;
import com.example.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    DuesRepository duesRepository;

    @InjectMocks
    PaymentService paymentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void makePaymentDuesNotFoundException() {
        Payment payment = new Payment();
        when(duesRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> paymentService.makePayment(1L, payment));
        Assertions.assertEquals("Borç kaydı bulunamadı", runtimeException.getMessage());
    }

    @Test
    public void makePaymentAmountException() {
        Payment payment = new Payment();
        payment.setAmount(BigDecimal.TEN);
        Dues dues = new Dues();
        dues.setAmount(BigDecimal.valueOf(10L));
        when(duesRepository.findById(1L)).thenReturn(Optional.of(dues));
        when(paymentRepository.findByDuesId(1L)).thenReturn(List.of(payment, payment, payment));

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> paymentService.makePayment(1L, payment));
        boolean deger = runtimeException.getMessage().contains("Fazla ödeme yapılamaz! Kalan borç: ");
        Assertions.assertTrue(deger);
    }

    @Test
    public void makePaymentTest() {
        Payment payment = new Payment();
        payment.setAmount(BigDecimal.TEN);
        Dues dues = new Dues();
        dues.setAmount(BigDecimal.valueOf(100L));
        when(duesRepository.findById(1L)).thenReturn(Optional.of(dues));
        when(paymentRepository.findByDuesId(1L)).thenReturn(List.of(payment, payment, payment));
        payment.setDues(dues);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        Payment dbPayment = paymentService.makePayment(1L, payment);

        Assertions.assertEquals(dbPayment.getAmount(), payment.getAmount());
        Assertions.assertEquals(dbPayment.getDues().getAmount(), payment.getDues().getAmount());
    }

    @Test
    public void getAllPaymentsTest() {
        Payment payment = new Payment();
        when(paymentRepository.findAll()).thenReturn(List.of(payment, payment));
        List<Payment> allPayments = paymentService.getAllPayments();

        Assertions.assertEquals(2, allPayments.size());
    }

    @Test
    public void getPaymentsByDuesIdTest() {
        Payment payment = new Payment();
        List list = List.of(payment, payment);
        when(paymentRepository.findByDuesId(1L)).thenReturn(list);
        List<Payment> paymentsByDuesId = paymentService.getPaymentsByDuesId(1L);

        Assertions.assertEquals(2, paymentsByDuesId.size());
    }

}
