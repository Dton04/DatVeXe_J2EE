package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshResponse {
    @JsonProperty("access_token")
    private String accessToken;

    public RefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
