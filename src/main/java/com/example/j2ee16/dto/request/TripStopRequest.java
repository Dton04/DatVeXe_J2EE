package com.example.j2ee16.dto.request;

import com.example.j2ee16.entity.StopType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class TripStopRequest {
    @NotNull(message = "Station ID is required")
    @JsonProperty("station_id")
    private Long stationId;

    @NotNull(message = "Stop type is required")
    @JsonProperty("stop_type")
    private StopType stopType;

    @NotNull(message = "Stop time is required")
    @JsonProperty("stop_time")
    private Instant stopTime;

    @JsonProperty("address_detail")
    private String addressDetail;

    @NotNull(message = "Order index is required")
    @JsonProperty("order_index")
    private Integer orderIndex;

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public StopType getStopType() {
        return stopType;
    }

    public void setStopType(StopType stopType) {
        this.stopType = stopType;
    }

    public Instant getStopTime() {
        return stopTime;
    }

    public void setStopTime(Instant stopTime) {
        this.stopTime = stopTime;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
