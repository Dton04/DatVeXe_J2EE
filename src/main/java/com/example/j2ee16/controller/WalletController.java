package com.example.j2ee16.controller;

import com.example.j2ee16.dto.request.WalletDepositRequest;
import com.example.j2ee16.dto.response.PaymentResponse;
import com.example.j2ee16.dto.response.WalletDTO;
import com.example.j2ee16.dto.response.WalletTransactionDTO;
import com.example.j2ee16.service.WalletService;
import com.example.j2ee16.repository.UserRepository;
import com.example.j2ee16.entity.User;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.constants.ErrorCodeConstants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;

    public WalletController(WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "User not found"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'STAFF')")
    public ResponseEntity<WalletDTO> getWallet(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        WalletDTO response = walletService.getWalletByUserId(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'STAFF')")
    public ResponseEntity<java.util.List<WalletTransactionDTO>> getTransactions(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(walletService.getTransactions(user.getId()));
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'STAFF')")
    public ResponseEntity<PaymentResponse> deposit(
            Authentication authentication,
            @Valid @RequestBody WalletDepositRequest request) {
        User user = getAuthenticatedUser(authentication);
        PaymentResponse response = walletService.initiateDeposit(user.getId(), request);
        return ResponseEntity.ok(response);
    }
}
