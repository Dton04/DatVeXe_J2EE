package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class TripResponse {
    private Long id;
    @JsonProperty("route_name")
    private String routeName;
    @JsonProperty("bus_plate")
    private String busPlate;
    @JsonProperty("departure_time")
    private Instant departureTime;
    @JsonProperty("actual_price")
    private BigDecimal actualPrice;

    public TripResponse() {
    }

    public TripResponse(Long id, String routeName, String busPlate, Instant departureTime, BigDecimal actualPrice) {
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

    public Instant getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }
}
