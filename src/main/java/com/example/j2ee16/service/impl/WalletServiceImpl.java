package com.example.j2ee16.service.impl;

import com.example.j2ee16.config.VNPayConfig;
import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.WalletDepositRequest;
import com.example.j2ee16.dto.response.PaymentResponse;
import com.example.j2ee16.dto.response.WalletDTO;
import com.example.j2ee16.entity.*;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.UserRepository;
import com.example.j2ee16.repository.WalletPromotionRuleRepository;
import com.example.j2ee16.repository.WalletRepository;
import com.example.j2ee16.repository.WalletTransactionRepository;
import com.example.j2ee16.repository.BookingRepository;
import com.example.j2ee16.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletPromotionRuleRepository ruleRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final VNPayConfig vnPayConfig;

    public WalletServiceImpl(WalletRepository walletRepository, 
                             WalletTransactionRepository walletTransactionRepository,
                             WalletPromotionRuleRepository ruleRepository,
                             UserRepository userRepository,
                             BookingRepository bookingRepository,
                             VNPayConfig vnPayConfig) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.ruleRepository = ruleRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.vnPayConfig = vnPayConfig;
    }

    private Wallet getOrCreateWallet(User user) {
        return walletRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUser(user);
                    w.setBalance(BigDecimal.ZERO);
                    return walletRepository.save(w);
                });
    }

    @Override
    public WalletDTO getWalletByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "User not found"));
        Wallet wallet = getOrCreateWallet(user);
        return new WalletDTO(wallet.getBalance());
    }

    @Override
    @Transactional
    public PaymentResponse initiateDeposit(Long userId, WalletDepositRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "User not found"));
        Wallet wallet = getOrCreateWallet(user);

        String transactionRef = "WAL-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(request.getAmount());
        tx.setType(TransactionType.DEPOSIT);
        tx.setStatus(PaymentStatus.PENDING);
        tx.setReference(transactionRef);
        tx.setDescription("Deposit to wallet");
        walletTransactionRepository.save(tx);

        // Build VNPAY URL
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getVnpTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(request.getAmount().multiply(new BigDecimal(100)).longValue()));
        vnp_Params.put("vnp_CurrCode", "VND");
        
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(java.time.ZoneId.of("GMT+7"));
        String vnp_CreateDate = formatter.format(java.time.Instant.now());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderInfo", "Nap tien vao vi " + transactionRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnp_Params.put("vnp_TxnRef", transactionRef);

        // Calculate Hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                try {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(java.net.URLEncoder.encode(fieldValue, java.nio.charset.StandardCharsets.US_ASCII.toString()));
                    query.append(java.net.URLEncoder.encode(fieldName, java.nio.charset.StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(java.net.URLEncoder.encode(fieldValue, java.nio.charset.StandardCharsets.US_ASCII.toString()));
                } catch (java.io.UnsupportedEncodingException e) {
                   // Ignore
                }
                
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        
        String queryUrl = query.toString();
        String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPayConfig.getVnpUrl() + "?" + queryUrl;

        return new PaymentResponse(paymentUrl, transactionRef);
    }

    @Override
    @Transactional
    public void handleDepositCallback(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        if (vnp_SecureHash != null) {
            params.remove("vnp_SecureHashType");
            params.remove("vnp_SecureHash");
            
            String signValue = vnPayConfig.hashAllFields(params);
            if (!signValue.equals(vnp_SecureHash)) {
                throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST, "Invalid Signature");
            }
        } else {
             throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST, "Missing Signature");
        }

        String transactionRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String status = "00".equals(responseCode) ? "SUCCESS" : "FAILED";

        WalletTransaction tx = walletTransactionRepository.findByReference(transactionRef)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Wallet deposit transaction not found"));

        if (tx.getStatus() == PaymentStatus.SUCCESS) {
            return; // Already processed
        }

        if ("SUCCESS".equalsIgnoreCase(status)) {
            tx.setStatus(PaymentStatus.SUCCESS);
            
            Wallet wallet = tx.getWallet();
            BigDecimal amount = tx.getAmount();
            
            // Calculate bonus
            List<WalletPromotionRule> rules = ruleRepository.findApplicableRules(amount);
            BigDecimal finalAmount = amount;
            if (!rules.isEmpty()) {
                WalletPromotionRule rule = rules.get(0); // get the best one
                if (rule.getBonusPercentage() != null && rule.getBonusPercentage() > 0) {
                    BigDecimal bonus = amount.multiply(new BigDecimal(rule.getBonusPercentage())).divide(new BigDecimal(100));
                    finalAmount = amount.add(bonus);
                }
            }
            
            wallet.setBalance(wallet.getBalance().add(finalAmount));
            walletRepository.save(wallet);
            
            tx.setDescription(tx.getDescription() + " - Added: " + finalAmount);
        } else {
            tx.setStatus(PaymentStatus.FAILED);
        }

        walletTransactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void payWithWallet(Long userId, Long bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "User not found"));
        Wallet wallet = getOrCreateWallet(user);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ApiException(ErrorCodeConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Booking not found"));

        if (wallet.getBalance().compareTo(booking.getTotalAmount()) < 0) {
            throw new ApiException(ErrorCodeConstants.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST, "Số dư ví không đủ. Vui lòng nạp thêm hoặc thanh toán qua VNPAY.");
        }

        // Deduct
        wallet.setBalance(wallet.getBalance().subtract(booking.getTotalAmount()));
        walletRepository.save(wallet);

        // Record Transaction
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(booking.getTotalAmount());
        tx.setType(TransactionType.PAYMENT);
        tx.setStatus(PaymentStatus.SUCCESS);
        tx.setReference("BKG-" + booking.getId());
        tx.setDescription("Paid for booking " + booking.getId());
        walletTransactionRepository.save(tx);
    }
}
