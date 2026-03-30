package com.example.j2ee16.dto.response;

import com.example.j2ee16.entity.StopType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class TripStopResponse {
    private Long id;
    @JsonProperty("station_name")
    private String stationName;
    @JsonProperty("province_name")
    private String provinceName;
    @JsonProperty("stop_type")
    private StopType stopType;
    @JsonProperty("stop_time")
    private Instant stopTime;
    @JsonProperty("address_detail")
    private String addressDetail;
    @JsonProperty("order_index")
    private Integer orderIndex;

    public TripStopResponse() {
    }

    public TripStopResponse(Long id, String stationName, String provinceName, StopType stopType, Instant stopTime, String addressDetail, Integer orderIndex) {
        this.id = id;
        this.stationName = stationName;
        this.provinceName = provinceName;
        this.stopType = stopType;
        this.stopTime = stopTime;
        this.addressDetail = addressDetail;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
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
