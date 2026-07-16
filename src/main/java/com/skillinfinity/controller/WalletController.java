package com.skillinfinity.controller;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.PurchaseCreditsRequestDTO;
import com.skillinfinity.dto.response.WalletResponseDTO;
import com.skillinfinity.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<ApiResponse<WalletResponseDTO>> getMyWallet() {

        return ResponseEntity.ok(
                walletService.getMyWallet()
        );
    }

    @PostMapping("/purchase")
    public ApiResponse<WalletResponseDTO> purchaseCredits(
            @Valid @RequestBody PurchaseCreditsRequestDTO requestDTO) {

        return walletService.purchaseCredits(requestDTO);
    }

}
