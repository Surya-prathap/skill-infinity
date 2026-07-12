package com.skillinfinity.service;

import com.skillinfinity.dto.*;

public interface AuthService {

    ApiResponse<RegisterResponseDTO> register(RegisterRequestDTO request);
    ApiResponse<LoginResponseDTO> login(LoginRequestDTO request);
    ApiResponse<UserResponseDTO> getCurrentUser();
    ApiResponse<UserResponseDTO> updateProfile(ProfileUpdateRequestDTO request);

}