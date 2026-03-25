package com.example.j2ee16.dto.response;

import com.example.j2ee16.entity.CheckInStatus;
import com.example.j2ee16.entity.TicketStatus;

import lombok.Data;


import java.math.BigDecimal;

@Data
public class TicketResponse {
    private Long id;
    private String seatNumber;
    private String passengerName;
    private String phone;
    private BigDecimal price;
    private TicketStatus ticketStatus;
    private CheckInStatus checkInStatus;

    public TicketResponse() {
    }

    public TicketResponse(Long id, String seatNumber, String passengerName, String phone, BigDecimal price, TicketStatus ticketStatus, CheckInStatus checkInStatus) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.passengerName = passengerName;
        this.phone = phone;
        this.price = price;
        this.ticketStatus = ticketStatus;
        this.checkInStatus = checkInStatus;
    }
}
