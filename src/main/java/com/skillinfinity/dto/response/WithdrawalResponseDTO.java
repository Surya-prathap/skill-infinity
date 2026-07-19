package com.skillinfinity.dto.response;

import com.skillinfinity.enums.WithdrawalStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalResponseDTO {

    private Long id;

    private String mentorName;

    private BigDecimal requestedCredits;

    private BigDecimal grossAmount;

    private BigDecimal platformCommission;

    private BigDecimal netAmount;

    private String upiId;

    private String remarks;

    private String adminRemarks;

    private WithdrawalStatus status;

    private LocalDateTime requestedAt;

    private LocalDateTime processedAt;

    private String processedBy;
}