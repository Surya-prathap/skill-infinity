package com.skillinfinity.service;

import com.skillinfinity.dto.ApiResponse;
import com.skillinfinity.dto.RegisterRequestDTO;
import com.skillinfinity.dto.RegisterResponseDTO;

public interface AuthService {

    ApiResponse<RegisterResponseDTO> register(RegisterRequestDTO request);

}