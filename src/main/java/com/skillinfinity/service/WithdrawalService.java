package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.WithdrawalRequestDTO;
import com.skillinfinity.dto.response.WithdrawalResponseDTO;

import java.util.List;

public interface WithdrawalService {

    ApiResponse<WithdrawalResponseDTO> requestWithdrawal(
            WithdrawalRequestDTO requestDTO
    );

    ApiResponse<List<WithdrawalResponseDTO>> getMyWithdrawalRequests();

    ApiResponse<List<WithdrawalResponseDTO>> getPendingWithdrawals();

    ApiResponse<WithdrawalResponseDTO> approveWithdrawal(
            Long withdrawalId, String remarks
    );

    ApiResponse<WithdrawalResponseDTO> rejectWithdrawal(
            Long withdrawalId,
            String remarks
    );
}