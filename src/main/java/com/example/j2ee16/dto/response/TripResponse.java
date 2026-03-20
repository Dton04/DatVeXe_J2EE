package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TripResponse {
    private Long id;
    @JsonProperty("route_name")
    private String routeName;
    @JsonProperty("bus_plate")
    private String busPlate;
    @JsonProperty("departure_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureTime;
    @JsonProperty("actual_price")
    private BigDecimal actualPrice;

    public TripResponse() {
    }

    public TripResponse(Long id, String routeName, String busPlate, LocalDateTime departureTime, BigDecimal actualPrice) {
        this.id = id;
        this.routeName = routeName;
        this.busPlate = busPlate;
        this.departureTime = departureTime;
        this.actualPrice = actualPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getBusPlate() {
        return busPlate;
    }

    public void setBusPlate(String busPlate) {
        this.busPlate = busPlate;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }
}
