package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.BookingRequest;
import com.example.j2ee16.dto.response.BookingResponse;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String username);
    void cancelExpiredHolds();
}
