package com.example.EntegrasyonTestleri;

import com.example.controller.PaymentController;
import com.example.entity.Payment;
import com.example.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PaymentService paymentService;

    @Test
    public void getAllPaymentsTest() throws Exception {
        Payment payment = new Payment();
        when(paymentService.getAllPayments()).thenReturn(List.of(payment, payment));

        mockMvc.perform(get("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getPaymentsByDuesTest() throws Exception {
        Long duesId = 1L;
        Payment payment = new Payment();
        when(paymentService.getPaymentsByDuesId(anyLong())).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/payments/dues/{duesId}", duesId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void makePaymentTest() throws Exception {
        Long duesId = 1L;
        Payment payment = new Payment();
        payment.setId(10L);
        when(paymentService.makePayment(anyLong(), any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/api/payments/dues/{duesId}", duesId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }
}
