package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.BookingRequest;
import com.example.j2ee16.dto.response.BookingResponse;
import com.example.j2ee16.dto.response.MyBookingResponse;
import com.example.j2ee16.dto.response.BookingDetailResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String username);
    void confirmCashPayment(Long bookingId);
    void completeBooking(Long bookingId);
    void cancelBooking(Long bookingId, String currentUserEmail);
    void cancelExpiredHolds();
    List<MyBookingResponse> getMyBookings(String email, String type);
    BookingDetailResponse getBookingDetail(Long bookingId, String email);
}
