package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.BookingRequest;
import com.example.j2ee16.dto.response.BookingResponse;
import com.example.j2ee16.dto.response.MyBookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String username);
    void confirmCashPayment(Long bookingId);
    void completeBooking(Long bookingId);
    void cancelBooking(Long bookingId);
    void cancelExpiredHolds();
    List<MyBookingResponse> getMyBookings(String email, String type);
}
