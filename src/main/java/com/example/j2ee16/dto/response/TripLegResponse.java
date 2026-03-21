package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class TripLegResponse {
    @JsonProperty("trip_id")
    private Long tripId;
    
    private String origin;
    private String destination;
    
    private Instant departure;
    private Instant arrival;
    
    @JsonProperty("bus_type")
    private String busType;

    public TripLegResponse() {
    }

    public TripLegResponse(Long tripId, String origin, String destination, Instant departure, Instant arrival, String busType) {
        this.tripId = tripId;
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.busType = busType;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Instant getDeparture() {
        return departure;
    }

    public void setDeparture(Instant departure) {
        this.departure = departure;
    }

    public Instant getArrival() {
        return arrival;
    }

    public void setArrival(Instant arrival) {
        this.arrival = arrival;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }
}
