package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.BookingRequestDTO;
import com.skillinfinity.dto.request.CommunityBookingRequestDTO;
import com.skillinfinity.dto.request.ProfessionalBookingRequestDTO;
import com.skillinfinity.dto.response.BookingResponseDTO;

import java.util.List;

public interface BookingService {

    ApiResponse<BookingResponseDTO> bookCommunitySession(
            CommunityBookingRequestDTO requestDTO);

    ApiResponse<BookingResponseDTO> bookProfessionalSession(
            ProfessionalBookingRequestDTO requestDTO);

    ApiResponse<List<BookingResponseDTO>> getMyBookings();

    ApiResponse<List<BookingResponseDTO>> getMentorBookings();

    ApiResponse<BookingResponseDTO> getBookingById(Long bookingId);

    ApiResponse<String> cancelBooking(Long bookingId);

    ApiResponse<String> completeBooking(Long bookingId);

}
