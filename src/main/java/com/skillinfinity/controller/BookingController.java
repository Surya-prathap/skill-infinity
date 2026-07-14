package com.skillinfinity.controller;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.BookingRequestDTO;
import com.skillinfinity.dto.response.BookingResponseDTO;
import com.skillinfinity.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
