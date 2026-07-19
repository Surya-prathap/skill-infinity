package com.skillinfinity.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalRequestDTO {

    @DecimalMin(value = "10.0", message = "Minimum withdrawal is 10 credits.")
    private BigDecimal requestedCredits;

    @NotBlank(message = "UPI ID is required.")
    private String upiId;

    private String remarks;

}