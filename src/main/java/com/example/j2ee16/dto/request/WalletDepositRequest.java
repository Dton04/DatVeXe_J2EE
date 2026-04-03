package com.example.j2ee16.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class WalletDepositRequest {
    @NotNull(message = "Amount is required")
    @Min(value = 10000, message = "Minimum deposit amount is 10,000 VND")
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
