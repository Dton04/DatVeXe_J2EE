package com.example.j2ee16.controller;

import com.example.j2ee16.dto.request.PaymentRequest;
import com.example.j2ee16.dto.response.PaymentResponse;
import com.example.j2ee16.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam Map<String, String> params) {
        paymentService.handlePaymentCallback(params);
        return ResponseEntity.ok("Payment processed. Status: " + params.get("vnp_ResponseCode"));
    }
}
