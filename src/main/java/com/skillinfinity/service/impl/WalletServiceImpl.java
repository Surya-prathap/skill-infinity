package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.PurchaseCreditsRequestDTO;
import com.skillinfinity.dto.response.WalletResponseDTO;
import com.skillinfinity.entity.User;
import com.skillinfinity.entity.Wallet;
import com.skillinfinity.enums.CreditType;
import com.skillinfinity.enums.TransactionCategory;
import com.skillinfinity.enums.TransactionType;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.repository.WalletRepository;
import com.skillinfinity.service.WalletService;
import com.skillinfinity.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;

    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;

    @Override
    public ApiResponse<WalletResponseDTO> getMyWallet() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found."));

        BigDecimal availableCredits = wallet.getWelcomeCredits()
                .add(wallet.getPurchasedCredits())
                .add(wallet.getLearningCredits());

        WalletResponseDTO responseDTO = WalletResponseDTO.builder()
                .welcomeCredits(wallet.getWelcomeCredits())
                .purchasedCredits(wallet.getPurchasedCredits())
                .learningCredits(wallet.getLearningCredits())
                .withdrawableCredits(wallet.getWithdrawableCredits())
                .availableCredits(availableCredits)
                .build();

        return ApiResponse.success(
                "Wallet fetched successfully.",
                responseDTO
        );
    }

    @Override
    public ApiResponse<WalletResponseDTO> purchaseCredits(
            PurchaseCreditsRequestDTO requestDTO) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found."));

        wallet.setPurchasedCredits(
                wallet.getPurchasedCredits()
                        .add(requestDTO.getCredits())
        );

        walletRepository.save(wallet);

        String reference = "PUR-" + System.currentTimeMillis();

        walletTransactionService.createTransaction(
                user,
                TransactionType.CREDIT,
                TransactionCategory.PURCHASE,
                CreditType.PURCHASED,
                requestDTO.getCredits(),
                reference
        );

        WalletResponseDTO responseDTO = WalletResponseDTO.builder()
                .welcomeCredits(wallet.getWelcomeCredits())
                .purchasedCredits(wallet.getPurchasedCredits())
                .learningCredits(wallet.getLearningCredits())
                .withdrawableCredits(wallet.getWithdrawableCredits())
                .build();

        return ApiResponse.success(
                "Credits purchased successfully.",
                responseDTO
        );
    }
}
