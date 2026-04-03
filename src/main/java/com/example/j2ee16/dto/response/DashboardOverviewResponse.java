package com.example.j2ee16.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class DashboardOverviewResponse {
    private BigDecimal todayRevenue;
    private double revenueGrowth; // %
    private double avgFillRate; // %
    private double fillRateGrowth; // %
    private long cancelledTickets;
    private double cancelledGrowth; // %
    private List<WeeklyRevenueResponse> weeklyRevenue;
    private List<ActiveTripResponse> activeTrips;

    // getters, setters, builder or constructors
    public DashboardOverviewResponse() {}

    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }

    public double getRevenueGrowth() { return revenueGrowth; }
    public void setRevenueGrowth(double revenueGrowth) { this.revenueGrowth = revenueGrowth; }

    public double getAvgFillRate() { return avgFillRate; }
    public void setAvgFillRate(double avgFillRate) { this.avgFillRate = avgFillRate; }

    public double getFillRateGrowth() { return fillRateGrowth; }
    public void setFillRateGrowth(double fillRateGrowth) { this.fillRateGrowth = fillRateGrowth; }

    public long getCancelledTickets() { return cancelledTickets; }
    public void setCancelledTickets(long cancelledTickets) { this.cancelledTickets = cancelledTickets; }

    public double getCancelledGrowth() { return cancelledGrowth; }
    public void setCancelledGrowth(double cancelledGrowth) { this.cancelledGrowth = cancelledGrowth; }

    public List<WeeklyRevenueResponse> getWeeklyRevenue() { return weeklyRevenue; }
    public void setWeeklyRevenue(List<WeeklyRevenueResponse> weeklyRevenue) { this.weeklyRevenue = weeklyRevenue; }

    public List<ActiveTripResponse> getActiveTrips() { return activeTrips; }
    public void setActiveTrips(List<ActiveTripResponse> activeTrips) { this.activeTrips = activeTrips; }
}
