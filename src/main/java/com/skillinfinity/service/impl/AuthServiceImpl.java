package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.LoginRequestDTO;
import com.skillinfinity.dto.request.ProfileUpdateRequestDTO;
import com.skillinfinity.dto.request.RegisterRequestDTO;
import com.skillinfinity.dto.response.LoginResponseDTO;
import com.skillinfinity.dto.response.RegisterResponseDTO;
import com.skillinfinity.dto.response.UserResponseDTO;
import com.skillinfinity.entity.User;
import com.skillinfinity.entity.Wallet;
import com.skillinfinity.exception.InvalidRequestException;
import com.skillinfinity.exception.ResourceAlreadyExistsException;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.security.JwtService;
import com.skillinfinity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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
                .password(passwordEncoder.encode(request.getPassword()))
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

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .build();

        RegisterResponseDTO response = RegisterResponseDTO.builder()
                .user(userResponse)
                .build();

        return ApiResponse.<RegisterResponseDTO>builder()
                .success(true)
                .message("Registration successful.")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<LoginResponseDTO> login(LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();

        LoginResponseDTO response = LoginResponseDTO.builder()
                .user(userResponse)
                .token(jwtService.generateToken(user.getEmail()))
                .build();

        return ApiResponse.<LoginResponseDTO>builder()
                .success(true)
                .message("Login successful.")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<UserResponseDTO> getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        UserResponseDTO response = UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();

        return ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .message("User fetched successfully.")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<UserResponseDTO> updateProfile(ProfileUpdateRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        user.setBio(request.getBio());
        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setCountry(request.getCountry());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setTimeZone(request.getTimeZone());

        User updatedUser = userRepository.save(user);

        UserResponseDTO response = UserResponseDTO.builder()
                .id(updatedUser.getId())
                .fullName(updatedUser.getFullName())
                .email(updatedUser.getEmail())
                .bio(updatedUser.getBio())
                .profileImageUrl(updatedUser.getProfileImageUrl())
                .country(updatedUser.getCountry())
                .state(updatedUser.getState())
                .city(updatedUser.getCity())
                .timeZone(updatedUser.getTimeZone())
                .build();

        return ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .message("Profile updated successfully.")
                .data(response)
                .build();
    }
}