package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.BookingRequestDTO;
import com.skillinfinity.dto.response.BookingResponseDTO;

import java.util.List;

public interface BookingService {

    ApiResponse<BookingResponseDTO> bookSession(
            BookingRequestDTO requestDTO);

    ApiResponse<List<BookingResponseDTO>> getMyBookings();

    ApiResponse<List<BookingResponseDTO>> getMentorBookings();

    ApiResponse<BookingResponseDTO> getBookingById(Long bookingId);

    ApiResponse<String> cancelBooking(Long bookingId);

}
