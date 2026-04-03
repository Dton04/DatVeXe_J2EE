package com.example.j2ee16.dto.response;

public class ActiveTripResponse {
    private Long id;
    private String tripCode;
    private String routeName;
    private String busPlate;
    private String driverName;
    private String departureTime; // "22:30 hôm nay"
    private int filledSeats;
    private int totalSeats;

    public ActiveTripResponse(Long id, String tripCode, String routeName, String busPlate, String driverName, String departureTime, int filledSeats, int totalSeats) {
        this.id = id;
        this.tripCode = tripCode;
        this.routeName = routeName;
        this.busPlate = busPlate;
        this.driverName = driverName;
        this.departureTime = departureTime;
        this.filledSeats = filledSeats;
        this.totalSeats = totalSeats;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTripCode() { return tripCode; }
    public void setTripCode(String tripCode) { this.tripCode = tripCode; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getBusPlate() { return busPlate; }
    public void setBusPlate(String busPlate) { this.busPlate = busPlate; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public int getFilledSeats() { return filledSeats; }
    public void setFilledSeats(int filledSeats) { this.filledSeats = filledSeats; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
}
