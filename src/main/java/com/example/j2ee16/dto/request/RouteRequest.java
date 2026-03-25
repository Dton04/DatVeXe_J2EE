package com.example.j2ee16.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class RouteRequest {
    @NotNull(message = "Origin station ID is required")
    @JsonProperty("origin_station_id")
    @JsonAlias("origin_id")
    private Long originStationId;

    @NotNull(message = "Destination station ID is required")
    @JsonProperty("destination_station_id")
    @JsonAlias("destination_id")
    private Long destinationStationId;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    @JsonProperty("base_price")
    @JsonAlias("price")
    private BigDecimal basePrice;

    @JsonProperty("distance_km")
    @JsonAlias("distance")
    @Positive(message = "Distance must be positive")
    private Double distanceKm;

    @JsonProperty("estimated_duration")
    @JsonAlias("duration")
    @Positive(message = "Estimated duration must be positive")
    private Integer estimatedDuration; // in minutes

    @JsonProperty("departure_date")
    private java.time.LocalDate departureDate;

    public Long getOriginStationId() {
        return originStationId;
    }

    public void setOriginStationId(Long originStationId) {
        this.originStationId = originStationId;
    }

    public Long getDestinationStationId() {
        return destinationStationId;
    }

    public void setDestinationStationId(Long destinationStationId) {
        this.destinationStationId = destinationStationId;
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

    public java.time.LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(java.time.LocalDate departureDate) {
        this.departureDate = departureDate;
    }
}
