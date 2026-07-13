package com.skillinfinity.controller;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.AvailabilityRequestDTO;
import com.skillinfinity.dto.response.AvailabilityResponseDTO;
import com.skillinfinity.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AvailabilityResponseDTO>>> getMyAvailability() {

        return ResponseEntity.ok(
                availabilityService.getMyAvailability()
        );
    }

    @PutMapping("/{availabilityId}")
    public ResponseEntity<ApiResponse<AvailabilityResponseDTO>> updateAvailability(
            @PathVariable Long availabilityId,
            @RequestBody AvailabilityRequestDTO requestDTO) {

        return ResponseEntity.ok(
                availabilityService.updateAvailability(
                        availabilityId,
                        requestDTO
                )
        );
    }

    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<ApiResponse<String>> deleteAvailability(
            @PathVariable Long availabilityId) {

        return ResponseEntity.ok(
                availabilityService.deleteAvailability(availabilityId)
        );
    }
}
