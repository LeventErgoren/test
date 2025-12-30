package com.example.controller;

import com.example.entity.Payment;
import com.example.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/dues/{duesId}")
    public ResponseEntity<List<Payment>> getPaymentsByDues(@PathVariable Long duesId) {
        return ResponseEntity.ok(paymentService.getPaymentsByDuesId(duesId));
    }

    // SENARYO: Fazla ödeme kontrolü burada tetiklenir
    @PostMapping("/dues/{duesId}")
    public ResponseEntity<Payment> makePayment(@PathVariable Long duesId, @RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.makePayment(duesId, payment));
    }
}
