package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.WalletResponseDTO;

public interface WalletService {

    ApiResponse<WalletResponseDTO> getMyWallet();
}
