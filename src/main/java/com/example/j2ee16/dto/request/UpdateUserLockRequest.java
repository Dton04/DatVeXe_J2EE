package com.example.j2ee16.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class UpdateUserLockRequest {
    @NotNull(message = "is_locked status is required")
    @JsonProperty("is_locked")
    private Boolean locked;

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
