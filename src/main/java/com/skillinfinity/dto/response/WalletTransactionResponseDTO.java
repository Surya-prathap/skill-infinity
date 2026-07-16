package com.skillinfinity.dto.response;

import com.skillinfinity.enums.CreditType;
import com.skillinfinity.enums.TransactionCategory;
import com.skillinfinity.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransactionResponseDTO {

    private TransactionType transactionType;

    private TransactionCategory transactionCategory;

    private CreditType creditType;

    private BigDecimal amount;

    private String reference;

    private LocalDateTime createdAt;
}