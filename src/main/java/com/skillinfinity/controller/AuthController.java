package com.skillinfinity.controller;

import com.skillinfinity.dto.ApiResponse;
import com.skillinfinity.dto.RegisterRequestDTO;
import com.skillinfinity.dto.RegisterResponseDTO;
import com.skillinfinity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        return ResponseEntity.ok(authService.register(request));
    }
}
