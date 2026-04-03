package com.example.j2ee16.dto.response;

import java.math.BigDecimal;

public class WalletDTO {
    private BigDecimal balance;

    public WalletDTO(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
