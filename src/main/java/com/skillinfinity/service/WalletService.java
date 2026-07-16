package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.PurchaseCreditsRequestDTO;
import com.skillinfinity.dto.response.WalletResponseDTO;

public interface WalletService {

    ApiResponse<WalletResponseDTO> getMyWallet();

    ApiResponse<WalletResponseDTO> purchaseCredits(
            PurchaseCreditsRequestDTO requestDTO
    );
}
