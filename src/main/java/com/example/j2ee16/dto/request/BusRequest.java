package com.example.j2ee16.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BusRequest {
    @NotBlank(message = "License plate is required")
    @Size(max = 20)
    @JsonProperty("license_plate")
    private String licensePlate;

    @NotNull(message = "Total seats count is required")
    @Min(value = 1, message = "Seats must be at least 1")
    @JsonProperty("total_seats")
    private Integer totalSeats;

    @JsonProperty("bus_type")
    @Size(max = 50)
    private String busType;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }
}
