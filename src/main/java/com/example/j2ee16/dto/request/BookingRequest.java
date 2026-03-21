package com.example.j2ee16.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class BookingRequest {
    @NotBlank(message = "Customer name is required")
    @JsonProperty("customer_name")
    private String customerName;

    @NotBlank(message = "Customer phone is required")
    @JsonProperty("customer_phone")
    private String customerPhone;

    @NotEmpty(message = "At least one trip leg is required")
    @Valid
    private List<BookingLegRequest> legs;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public List<BookingLegRequest> getLegs() {
        return legs;
    }

    public void setLegs(List<BookingLegRequest> legs) {
        this.legs = legs;
    }
}
