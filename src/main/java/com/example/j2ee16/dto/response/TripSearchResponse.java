package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripSearchResponse {
    private String type; // "DIRECT" or "CONNECTING"
    
    @JsonProperty("total_price")
    private BigDecimal totalPrice;
    
    @JsonProperty("layover_time")
    private String layoverTime; // e.g., "90 mins"
    
    private List<TripLegResponse> legs;

    public TripSearchResponse() {
    }

    public TripSearchResponse(String type, BigDecimal totalPrice, String layoverTime, List<TripLegResponse> legs) {
        this.type = type;
        this.totalPrice = totalPrice;
        this.layoverTime = layoverTime;
        this.legs = legs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getLayoverTime() {
        return layoverTime;
    }

    public void setLayoverTime(String layoverTime) {
        this.layoverTime = layoverTime;
    }

    public List<TripLegResponse> getLegs() {
        return legs;
    }

    public void setLegs(List<TripLegResponse> legs) {
        this.legs = legs;
    }
}
