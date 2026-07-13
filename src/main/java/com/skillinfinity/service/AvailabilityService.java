package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.AvailabilityRequestDTO;
import com.skillinfinity.dto.response.AvailabilityResponseDTO;

public interface AvailabilityService {

    ApiResponse<AvailabilityResponseDTO> addAvailability(
            AvailabilityRequestDTO requestDTO);

}
