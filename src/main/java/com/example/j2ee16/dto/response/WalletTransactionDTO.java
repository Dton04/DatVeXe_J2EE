package com.example.j2ee16.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public class WalletTransactionDTO {
    private Long id;
    private BigDecimal amount;
    private String type; // DEPOSIT, PAYMENT
    private String status;
    private String reference;
    private String description;
    private Instant createdAt;

    public WalletTransactionDTO() {
    }

    public WalletTransactionDTO(Long id, BigDecimal amount, String type, String status, String reference, String description, Instant createdAt) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.reference = reference;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
