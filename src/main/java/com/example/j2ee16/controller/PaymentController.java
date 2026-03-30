package com.example.j2ee16.controller;

import com.example.j2ee16.dto.request.PaymentRequest;
import com.example.j2ee16.dto.response.PaymentResponse;
import com.example.j2ee16.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    private final PaymentService paymentService;
    private final String frontendBaseUrl;

    public PaymentController(PaymentService paymentService, @Value("${app.frontend.base-url}") String frontendBaseUrl) {
        this.paymentService = paymentService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    public RedirectView handleCallback(@RequestParam Map<String, String> params) {
        String responseCode = params.getOrDefault("vnp_ResponseCode", "");
        String txnRef = params.getOrDefault("vnp_TxnRef", "");

        String paymentStatus = "00".equals(responseCode) ? "success" : "failed";
        try {
            paymentService.handlePaymentCallback(params);
        } catch (Exception ex) {
            paymentStatus = "failed";
        }

        String redirectUrl = UriComponentsBuilder.fromHttpUrl(frontendBaseUrl)
                .path("/payment/vnpay/return")
                .queryParam("status", paymentStatus)
                .queryParam("txnRef", txnRef)
                .build()
                .toUriString();

        RedirectView view = new RedirectView(redirectUrl);
        view.setStatusCode(HttpStatus.FOUND);
        return view;
    }

    @GetMapping("/vnpay/callback")
    public RedirectView handleVnPayCallback(@RequestParam Map<String, String> params) {
        return handleCallback(params);
    }
}
