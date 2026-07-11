package com.skillinfinity.service.impl;

import com.skillinfinity.dto.ApiResponse;
import com.skillinfinity.dto.RegisterRequestDTO;
import com.skillinfinity.dto.RegisterResponseDTO;
import com.skillinfinity.entity.User;
import com.skillinfinity.entity.Wallet;
import com.skillinfinity.exception.InvalidRequestException;
import com.skillinfinity.exception.ResourceAlreadyExistsException;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public ApiResponse<RegisterResponseDTO> register(RegisterRequestDTO request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidRequestException("Passwords do not match.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists.");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        Wallet wallet = Wallet.builder()
                .starterCredits(BigDecimal.valueOf(5))
                .learningCredits(BigDecimal.ZERO)
                .purchasedCredits(BigDecimal.ZERO)
                .withdrawableCredits(BigDecimal.ZERO)
                .build();

        wallet.setUser(user);
        user.setWallet(wallet);

        User savedUser = userRepository.save(user);

        RegisterResponseDTO response = RegisterResponseDTO.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .message("User registered successfully.")
                .build();

        return ApiResponse.<RegisterResponseDTO>builder()
                .success(true)
                .message("Registration successful.")
                .data(response)
                .build();
    }
}