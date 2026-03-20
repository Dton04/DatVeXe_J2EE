package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StaffResponse {
    private Long id;
    private String email;
    @JsonProperty("is_locked")
    private boolean locked;

    public StaffResponse() {
    }

    public StaffResponse(Long id, String email, boolean locked) {
        this.id = id;
        this.email = email;
        this.locked = locked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
