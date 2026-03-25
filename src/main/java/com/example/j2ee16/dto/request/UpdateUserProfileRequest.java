package com.example.j2ee16.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateUserProfileRequest {
    @JsonProperty("full_name")
    private String fullName;

    private String phone;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
