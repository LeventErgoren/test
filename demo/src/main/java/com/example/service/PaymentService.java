package com.example.service;

import com.example.entity.Payment;
import com.example.entity.Dues;
import com.example.repository.PaymentRepository;
import com.example.repository.DuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DuesRepository duesRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByDuesId(Long duesId) {
        return paymentRepository.findByDuesId(duesId);
    }

    public Payment makePayment(Long duesId, Payment payment) {
        Dues dues = duesRepository.findById(duesId)
                .orElseThrow(() -> new RuntimeException("Borç kaydı bulunamadı"));

        List<Payment> existingPayments = paymentRepository.findByDuesId(duesId);
        
        BigDecimal totalPaid = existingPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingDebt = dues.getAmount().subtract(totalPaid);

        if (payment.getAmount().compareTo(remainingDebt) > 0) {
            throw new RuntimeException("Fazla ödeme yapılamaz! Kalan borç: " + remainingDebt);
        }

        payment.setDues(dues);
        return paymentRepository.save(payment);
    }
}
