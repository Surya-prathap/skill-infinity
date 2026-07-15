package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.WalletResponseDTO;
import com.skillinfinity.entity.User;
import com.skillinfinity.entity.Wallet;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.repository.WalletRepository;
import com.skillinfinity.service.WalletService;
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

        BigDecimal availableCredits = wallet.getStarterCredits()
                .add(wallet.getPurchasedCredits())
                .add(wallet.getLearningCredits());

        WalletResponseDTO responseDTO = WalletResponseDTO.builder()
                .starterCredits(wallet.getStarterCredits())
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
}
