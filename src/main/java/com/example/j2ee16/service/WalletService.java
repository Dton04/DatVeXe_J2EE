package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.WalletDepositRequest;
import com.example.j2ee16.dto.response.PaymentResponse;
import com.example.j2ee16.dto.response.WalletDTO;

import java.util.Map;

public interface WalletService {
    WalletDTO getWalletByUserId(Long userId);
    PaymentResponse initiateDeposit(Long userId, WalletDepositRequest request);
    void handleDepositCallback(Map<String, String> params);
    void payWithWallet(Long userId, Long bookingId);
}
