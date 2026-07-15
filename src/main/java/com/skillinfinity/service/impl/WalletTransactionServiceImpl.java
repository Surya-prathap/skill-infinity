package com.skillinfinity.service.impl;

import com.skillinfinity.entity.User;
import com.skillinfinity.entity.WalletTransaction;
import com.skillinfinity.enums.CreditType;
import com.skillinfinity.enums.TransactionCategory;
import com.skillinfinity.enums.TransactionType;
import com.skillinfinity.repository.WalletTransactionRepository;
import com.skillinfinity.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl
        implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;

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
}