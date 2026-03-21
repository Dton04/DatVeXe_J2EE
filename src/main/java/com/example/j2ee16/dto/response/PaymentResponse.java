package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentResponse {
    @JsonProperty("payment_url")
    private String paymentUrl;
    
    @JsonProperty("transaction_ref")
    private String transactionRef;

    public PaymentResponse(String paymentUrl, String transactionRef) {
        this.paymentUrl = paymentUrl;
        this.transactionRef = transactionRef;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }
}
