package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class RouteResponse {
    private Long id;
    private String name;
    @JsonProperty("base_price")
    private BigDecimal basePrice;

    @JsonProperty("distance_km")
    private Double distanceKm;

    @JsonProperty("estimated_duration")
    private Integer estimatedDuration;

    @JsonProperty("origin_station")
    private StationResponse originStation;

    @JsonProperty("destination_station")
    private StationResponse destinationStation;

    public RouteResponse() {
    }

    public RouteResponse(Long id, String name, BigDecimal basePrice, Double distanceKm, Integer estimatedDuration, StationResponse originStation, StationResponse destinationStation) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.distanceKm = distanceKm;
        this.estimatedDuration = estimatedDuration;
        this.originStation = originStation;
        this.destinationStation = destinationStation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public StationResponse getOriginStation() {
        return originStation;
    }

    public void setOriginStation(StationResponse originStation) {
        this.originStation = originStation;
    }

    public StationResponse getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(StationResponse destinationStation) {
        this.destinationStation = destinationStation;
    }
}
