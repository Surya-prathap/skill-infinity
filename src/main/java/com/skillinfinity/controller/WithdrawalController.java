package com.skillinfinity.controller;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.WithdrawalRequestDTO;
import com.skillinfinity.dto.response.WithdrawalResponseDTO;
import com.skillinfinity.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    // ===========================
    // Mentor Endpoints
    // ===========================

    @PostMapping("/api/withdrawals/request")
    public ApiResponse<WithdrawalResponseDTO> requestWithdrawal(
            @Valid @RequestBody WithdrawalRequestDTO requestDTO) {

        return withdrawalService.requestWithdrawal(requestDTO);
    }

    @GetMapping("/api/withdrawals/my-requests")
    public ApiResponse<List<WithdrawalResponseDTO>> getMyWithdrawalRequests() {

        return withdrawalService.getMyWithdrawalRequests();
    }

    // ===========================
    // Admin Endpoints
    // ===========================

    @GetMapping("/api/admin/withdrawals/pending")
    public ApiResponse<List<WithdrawalResponseDTO>> getPendingWithdrawals() {

        return withdrawalService.getPendingWithdrawals();
    }

    @PutMapping("/api/admin/withdrawals/{withdrawalId}/approve")
    public ApiResponse<WithdrawalResponseDTO> approveWithdrawal(
            @PathVariable Long withdrawalId,
            @RequestParam(required = false) String remarks) {

        return withdrawalService.approveWithdrawal(withdrawalId, remarks);
    }

    @PutMapping("/api/admin/withdrawals/{withdrawalId}/reject")
    public ApiResponse<WithdrawalResponseDTO> rejectWithdrawal(
            @PathVariable Long withdrawalId,
            @RequestParam String remarks) {

        return withdrawalService.rejectWithdrawal(withdrawalId, remarks);
    }
}