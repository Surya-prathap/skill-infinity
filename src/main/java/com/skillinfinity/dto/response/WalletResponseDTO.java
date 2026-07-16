package com.skillinfinity.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponseDTO {

    private BigDecimal welcomeCredits;

    private BigDecimal purchasedCredits;

    private BigDecimal learningCredits;

    private BigDecimal withdrawableCredits;

    private BigDecimal availableCredits;
}
