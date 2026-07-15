package com.skillinfinity.service;

import com.skillinfinity.entity.User;
import com.skillinfinity.enums.CreditType;
import com.skillinfinity.enums.TransactionCategory;
import com.skillinfinity.enums.TransactionType;

import java.math.BigDecimal;

public interface WalletTransactionService {

    void createTransaction(
            User user,
            TransactionType transactionType,
            TransactionCategory transactionCategory,
            CreditType creditType,
            BigDecimal amount,
            String reference
    );

}