package com.example.j2ee16.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StationRequest {
    @NotBlank(message = "Station name is required")
    @Size(max = 120)
    private String name;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @Size(max = 255)
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
