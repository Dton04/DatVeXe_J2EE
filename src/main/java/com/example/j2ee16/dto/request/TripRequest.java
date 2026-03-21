package com.example.j2ee16.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public class TripRequest {
    @NotNull(message = "Route ID is required")
    @JsonProperty("route_id")
    private Long routeId;

    @NotNull(message = "Bus ID is required")
    @JsonProperty("bus_id")
    private Long busId;

    @NotNull(message = "Departure time is required")
    @JsonProperty("departure_time")
    private Instant departureTime;

    @NotNull(message = "Price modifier is required")
    @JsonProperty("price_modifier")
    private BigDecimal priceModifier;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public Instant getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public BigDecimal getPriceModifier() {
        return priceModifier;
    }

    public void setPriceModifier(BigDecimal priceModifier) {
        this.priceModifier = priceModifier;
    }
}
