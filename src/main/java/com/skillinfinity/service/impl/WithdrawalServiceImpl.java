package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.WithdrawalRequestDTO;
import com.skillinfinity.dto.response.WithdrawalResponseDTO;
import com.skillinfinity.entity.User;
import com.skillinfinity.entity.Wallet;
import com.skillinfinity.entity.WithdrawalRequest;
import com.skillinfinity.enums.CreditType;
import com.skillinfinity.enums.TransactionCategory;
import com.skillinfinity.enums.TransactionType;
import com.skillinfinity.enums.WithdrawalStatus;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.repository.WalletRepository;
import com.skillinfinity.repository.WithdrawalRepository;
import com.skillinfinity.service.WalletTransactionService;
import com.skillinfinity.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;

    @Override
    public ApiResponse<WithdrawalResponseDTO> requestWithdrawal(
            WithdrawalRequestDTO requestDTO) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User mentor = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Wallet wallet = walletRepository.findByUser(mentor)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found."));

        BigDecimal requestedCredits = requestDTO.getRequestedCredits();

        if (requestedCredits.compareTo(BigDecimal.TEN) < 0) {
            throw new IllegalArgumentException(
                    "Minimum withdrawal is 10 credits.");
        }

        if (wallet.getWithdrawableCredits().compareTo(requestedCredits) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient withdrawable credits.");
        }

        BigDecimal grossAmount = requestedCredits.multiply(BigDecimal.TEN);

        BigDecimal platformCommission = grossAmount
                .multiply(new BigDecimal("0.20"))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal netAmount = grossAmount.subtract(platformCommission);

        WithdrawalRequest withdrawalRequest = WithdrawalRequest.builder()
                .mentor(mentor)
                .requestedCredits(requestedCredits)
                .grossAmount(grossAmount)
                .platformCommission(platformCommission)
                .netAmount(netAmount)
                .upiId(requestDTO.getUpiId())
                .remarks(requestDTO.getRemarks())
                .build();

        withdrawalRequest = withdrawalRepository.save(withdrawalRequest);

        WithdrawalResponseDTO responseDTO = WithdrawalResponseDTO.builder()
                .id(withdrawalRequest.getId())
                .mentorName(mentor.getFullName())
                .requestedCredits(withdrawalRequest.getRequestedCredits())
                .grossAmount(withdrawalRequest.getGrossAmount())
                .platformCommission(withdrawalRequest.getPlatformCommission())
                .netAmount(withdrawalRequest.getNetAmount())
                .upiId(withdrawalRequest.getUpiId())
                .remarks(withdrawalRequest.getRemarks())
                .adminRemarks(withdrawalRequest.getAdminRemarks())
                .status(withdrawalRequest.getStatus())
                .requestedAt(withdrawalRequest.getRequestedAt())
                .processedAt(withdrawalRequest.getProcessedAt())
                .processedBy(null)
                .build();

        return ApiResponse.success(
                "Withdrawal request submitted successfully.",
                responseDTO
        );
    }

    @Override
    public ApiResponse<List<WithdrawalResponseDTO>> getMyWithdrawalRequests() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User mentor = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        List<WithdrawalRequest> withdrawalRequests =
                withdrawalRepository.findByMentorOrderByRequestedAtDesc(mentor);

        List<WithdrawalResponseDTO> response = withdrawalRequests.stream()
                .map(request -> WithdrawalResponseDTO.builder()
                        .id(request.getId())
                        .mentorName(request.getMentor().getFullName())
                        .requestedCredits(request.getRequestedCredits())
                        .grossAmount(request.getGrossAmount())
                        .platformCommission(request.getPlatformCommission())
                        .netAmount(request.getNetAmount())
                        .upiId(request.getUpiId())
                        .remarks(request.getRemarks())
                        .adminRemarks(request.getAdminRemarks())
                        .status(request.getStatus())
                        .requestedAt(request.getRequestedAt())
                        .processedAt(request.getProcessedAt())
                        .processedBy(
                                request.getProcessedBy() != null
                                        ? request.getProcessedBy().getFullName()
                                        : null
                        )
                        .build())
                .toList();

        return ApiResponse.success(
                "Withdrawal requests fetched successfully.",
                response
        );
    }

    @Override
    public ApiResponse<List<WithdrawalResponseDTO>> getPendingWithdrawals() {

        List<WithdrawalRequest> withdrawalRequests =
                withdrawalRepository.findByStatusOrderByRequestedAtAsc(
                        WithdrawalStatus.PENDING
                );

        List<WithdrawalResponseDTO> response = withdrawalRequests.stream()
                .map(request -> WithdrawalResponseDTO.builder()
                        .id(request.getId())
                        .mentorName(request.getMentor().getFullName())
                        .requestedCredits(request.getRequestedCredits())
                        .grossAmount(request.getGrossAmount())
                        .platformCommission(request.getPlatformCommission())
                        .netAmount(request.getNetAmount())
                        .upiId(request.getUpiId())
                        .remarks(request.getRemarks())
                        .adminRemarks(request.getAdminRemarks())
                        .status(request.getStatus())
                        .requestedAt(request.getRequestedAt())
                        .processedAt(request.getProcessedAt())
                        .processedBy(
                                request.getProcessedBy() != null
                                        ? request.getProcessedBy().getFullName()
                                        : null
                        )
                        .build())
                .toList();

        return ApiResponse.success(
                "Pending withdrawal requests fetched successfully.",
                response
        );
    }

    @Override
    public ApiResponse<WithdrawalResponseDTO> approveWithdrawal(
            Long withdrawalId,
            String remarks) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin not found."));

        WithdrawalRequest withdrawalRequest = withdrawalRepository
                .findById(withdrawalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Withdrawal request not found."));

        if (withdrawalRequest.getStatus() != WithdrawalStatus.PENDING) {
            throw new IllegalArgumentException(
                    "This withdrawal request has already been processed.");
        }

        Wallet wallet = walletRepository.findByUser(
                        withdrawalRequest.getMentor())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found."));

        if (wallet.getWithdrawableCredits()
                .compareTo(withdrawalRequest.getRequestedCredits()) < 0) {

            throw new IllegalArgumentException(
                    "Mentor does not have sufficient withdrawable credits.");
        }

        wallet.setWithdrawableCredits(
                wallet.getWithdrawableCredits()
                        .subtract(withdrawalRequest.getRequestedCredits())
        );

        walletRepository.save(wallet);

        String reference = "WDR-" + System.currentTimeMillis();

        walletTransactionService.createTransaction(
                withdrawalRequest.getMentor(),
                TransactionType.DEBIT,
                TransactionCategory.WITHDRAWAL,
                CreditType.WITHDRAWABLE,
                withdrawalRequest.getRequestedCredits(),
                reference
        );

        withdrawalRequest.setStatus(WithdrawalStatus.APPROVED);
        withdrawalRequest.setProcessedBy(admin);
        withdrawalRequest.setProcessedAt(LocalDateTime.now());
        withdrawalRequest.setAdminRemarks(remarks);

        withdrawalRepository.save(withdrawalRequest);

        WithdrawalResponseDTO responseDTO =
                WithdrawalResponseDTO.builder()
                        .id(withdrawalRequest.getId())
                        .mentorName(withdrawalRequest.getMentor().getFullName())
                        .requestedCredits(withdrawalRequest.getRequestedCredits())
                        .grossAmount(withdrawalRequest.getGrossAmount())
                        .platformCommission(withdrawalRequest.getPlatformCommission())
                        .netAmount(withdrawalRequest.getNetAmount())
                        .upiId(withdrawalRequest.getUpiId())
                        .remarks(withdrawalRequest.getRemarks())
                        .adminRemarks(withdrawalRequest.getAdminRemarks())
                        .status(withdrawalRequest.getStatus())
                        .requestedAt(withdrawalRequest.getRequestedAt())
                        .processedAt(withdrawalRequest.getProcessedAt())
                        .processedBy(admin.getFullName())
                        .build();

        return ApiResponse.success(
                "Withdrawal approved successfully.",
                responseDTO
        );
    }

    @Override
    public ApiResponse<WithdrawalResponseDTO> rejectWithdrawal(
            Long withdrawalId,
            String remarks) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin not found."));

        WithdrawalRequest withdrawalRequest = withdrawalRepository
                .findById(withdrawalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Withdrawal request not found."));

        if (withdrawalRequest.getStatus() != WithdrawalStatus.PENDING) {
            throw new IllegalArgumentException(
                    "This withdrawal request has already been processed.");
        }

        withdrawalRequest.setStatus(WithdrawalStatus.REJECTED);
        withdrawalRequest.setProcessedBy(admin);
        withdrawalRequest.setProcessedAt(LocalDateTime.now());
        withdrawalRequest.setAdminRemarks(remarks);

        withdrawalRepository.save(withdrawalRequest);

        WithdrawalResponseDTO responseDTO =
                WithdrawalResponseDTO.builder()
                        .id(withdrawalRequest.getId())
                        .mentorName(withdrawalRequest.getMentor().getFullName())
                        .requestedCredits(withdrawalRequest.getRequestedCredits())
                        .grossAmount(withdrawalRequest.getGrossAmount())
                        .platformCommission(withdrawalRequest.getPlatformCommission())
                        .netAmount(withdrawalRequest.getNetAmount())
                        .upiId(withdrawalRequest.getUpiId())
                        .remarks(withdrawalRequest.getRemarks())
                        .adminRemarks(withdrawalRequest.getAdminRemarks())
                        .status(withdrawalRequest.getStatus())
                        .requestedAt(withdrawalRequest.getRequestedAt())
                        .processedAt(withdrawalRequest.getProcessedAt())
                        .processedBy(admin.getFullName())
                        .build();

        return ApiResponse.success(
                "Withdrawal request rejected successfully.",
                responseDTO
        );
    }

}