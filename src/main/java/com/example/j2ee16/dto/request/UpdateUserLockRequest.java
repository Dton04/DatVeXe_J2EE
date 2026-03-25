package com.example.j2ee16.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;

public class UpdateUserLockRequest {
    @NotNull(message = "locked status is required")
    @JsonAlias("is_locked")
    private Boolean locked;

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
