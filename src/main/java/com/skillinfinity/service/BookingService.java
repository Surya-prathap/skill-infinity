package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.BookingRequestDTO;
import com.skillinfinity.dto.response.BookingResponseDTO;

public interface BookingService {

    ApiResponse<BookingResponseDTO> bookSession(
            BookingRequestDTO requestDTO);

}
