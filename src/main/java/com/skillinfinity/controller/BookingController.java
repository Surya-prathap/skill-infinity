package com.skillinfinity.controller;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.BookingRequestDTO;
import com.skillinfinity.dto.response.BookingResponseDTO;
import com.skillinfinity.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> bookSession(
            @RequestBody BookingRequestDTO requestDTO) {

        return ResponseEntity.ok(
                bookingService.bookSession(requestDTO)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getMyBookings() {

        return ResponseEntity.ok(
                bookingService.getMyBookings()
        );
    }

    @GetMapping("/mentor")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getMentorBookings() {

        return ResponseEntity.ok(
                bookingService.getMentorBookings()
        );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingById(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.getBookingById(bookingId)
        );
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(bookingId)
        );
    }
}
