package com.skillinfinity.controller;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.WalletTransactionResponseDTO;
import com.skillinfinity.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletTransactionController {

    private final WalletTransactionService walletTransactionService;

    @GetMapping("/history")
    public ApiResponse<List<WalletTransactionResponseDTO>> getTransactionHistory() {

        return walletTransactionService.getTransactionHistory();

    }
}
