package com.skillinfinity.controller;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.AvailabilityRequestDTO;
import com.skillinfinity.dto.response.AvailabilityResponseDTO;
import com.skillinfinity.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<ApiResponse<AvailabilityResponseDTO>> addAvailability(
            @RequestBody AvailabilityRequestDTO requestDTO) {

        return ResponseEntity.ok(
                availabilityService.addAvailability(requestDTO)
        );
    }
}
