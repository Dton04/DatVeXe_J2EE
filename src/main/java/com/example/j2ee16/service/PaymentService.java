package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.PaymentRequest;
import com.example.j2ee16.dto.response.PaymentResponse;

import java.util.Map;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    void handlePaymentCallback(Map<String, String> params);
}
