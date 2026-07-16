package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.WalletTransactionResponseDTO;
import com.skillinfinity.entity.User;
import com.skillinfinity.enums.CreditType;
import com.skillinfinity.enums.TransactionCategory;
import com.skillinfinity.enums.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public interface WalletTransactionService {

    void createTransaction(
            User user,
            TransactionType transactionType,
            TransactionCategory transactionCategory,
            CreditType creditType,
            BigDecimal amount,
            String reference
    );

    ApiResponse<List<WalletTransactionResponseDTO>> getTransactionHistory();

}