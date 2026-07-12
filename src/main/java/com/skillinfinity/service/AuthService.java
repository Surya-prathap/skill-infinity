package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.LoginRequestDTO;
import com.skillinfinity.dto.request.ProfileUpdateRequestDTO;
import com.skillinfinity.dto.request.RegisterRequestDTO;
import com.skillinfinity.dto.response.LoginResponseDTO;
import com.skillinfinity.dto.response.RegisterResponseDTO;
import com.skillinfinity.dto.response.UserResponseDTO;

public interface AuthService {

    ApiResponse<RegisterResponseDTO> register(RegisterRequestDTO request);
    ApiResponse<LoginResponseDTO> login(LoginRequestDTO request);
    ApiResponse<UserResponseDTO> getCurrentUser();
    ApiResponse<UserResponseDTO> updateProfile(ProfileUpdateRequestDTO request);

}