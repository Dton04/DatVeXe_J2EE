package com.example.j2ee16.dto.response;

import java.math.BigDecimal;

public class WeeklyRevenueResponse {
    private String dayName;
    private BigDecimal revenue;

    public WeeklyRevenueResponse(String dayName, BigDecimal revenue) {
        this.dayName = dayName;
        this.revenue = revenue;
    }

    public String getDayName() { return dayName; }
    public void setDayName(String dayName) { this.dayName = dayName; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
