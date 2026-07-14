package com.skillinfinity.dto.common;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditDeductionResult {

    private BigDecimal purchasedCreditsUsed;

    private BigDecimal starterCreditsUsed;

    private BigDecimal learningCreditsUsed;
}
