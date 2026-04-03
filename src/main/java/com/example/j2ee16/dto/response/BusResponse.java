package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BusResponse {
    private Long id;
    @JsonProperty("license_plate")
    private String licensePlate;
    @JsonProperty("total_seats")
    private Integer totalSeats;
    @JsonProperty("bus_type")
    private String busType;
    @JsonProperty("seats_generated")
    private Integer seatsGenerated;

    public BusResponse() {
    }

    public BusResponse(Long id, String licensePlate, Integer totalSeats, String busType) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.totalSeats = totalSeats;
        this.busType = busType;
        this.seatsGenerated = null;
    }

    public BusResponse(Long id, String licensePlate, Integer totalSeats, String busType, Integer seatsGenerated) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.totalSeats = totalSeats;
        this.busType = busType;
        this.seatsGenerated = seatsGenerated;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }

    public Integer getSeatsGenerated() { return seatsGenerated; }
    public void setSeatsGenerated(Integer seatsGenerated) { this.seatsGenerated = seatsGenerated; }
}
