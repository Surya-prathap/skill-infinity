package com.skillinfinity.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseCreditsRequestDTO {

    @NotNull(message = "Credits are required.")
    @DecimalMin(value = "1.0", message = "Credits must be greater than zero.")
    private BigDecimal credits;
}
