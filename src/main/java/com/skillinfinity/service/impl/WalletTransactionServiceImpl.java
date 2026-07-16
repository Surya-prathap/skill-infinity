package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.WalletTransactionResponseDTO;
import com.skillinfinity.entity.User;
import com.skillinfinity.entity.WalletTransaction;
import com.skillinfinity.enums.CreditType;
import com.skillinfinity.enums.TransactionCategory;
import com.skillinfinity.enums.TransactionType;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.repository.WalletTransactionRepository;
import com.skillinfinity.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl
        implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    @Override
    public void createTransaction(
            User user,
            TransactionType transactionType,
            TransactionCategory transactionCategory,
            CreditType creditType,
            BigDecimal amount,
            String reference
    ) {

        WalletTransaction transaction = WalletTransaction.builder()
                .user(user)
                .transactionType(transactionType)
                .transactionCategory(transactionCategory)
                .creditType(creditType)
                .amount(amount)
                .reference(reference)
                .build();

        walletTransactionRepository.save(transaction);
    }

    @Override
    public ApiResponse<List<WalletTransactionResponseDTO>> getTransactionHistory() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        List<WalletTransaction> transactions =
                walletTransactionRepository.findByUserOrderByCreatedAtDesc(user);

        List<WalletTransactionResponseDTO> response = transactions.stream()
                .map(transaction -> WalletTransactionResponseDTO.builder()
                        .transactionType(transaction.getTransactionType())
                        .transactionCategory(transaction.getTransactionCategory())
                        .creditType(transaction.getCreditType())
                        .amount(transaction.getAmount())
                        .reference(transaction.getReference())
                        .createdAt(transaction.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success(
                "Wallet transaction history fetched successfully.",
                response
        );
    }
}