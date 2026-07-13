package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.AvailabilityRequestDTO;
import com.skillinfinity.dto.response.AvailabilityResponseDTO;

import java.util.List;

public interface AvailabilityService {

    ApiResponse<AvailabilityResponseDTO> addAvailability(
            AvailabilityRequestDTO requestDTO);

    ApiResponse<List<AvailabilityResponseDTO>> getMyAvailability();

    ApiResponse<AvailabilityResponseDTO> updateAvailability(
            Long availabilityId,
            AvailabilityRequestDTO requestDTO);

    ApiResponse<String> deleteAvailability(Long availabilityId);

}
