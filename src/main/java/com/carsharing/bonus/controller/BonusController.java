package com.carsharing.bonus.controller;

import com.carsharing.bonus.dto.BonusBalanceResponse;
import com.carsharing.bonus.dto.BonusTransactionResponse;
import com.carsharing.bonus.service.BonusService;
import com.carsharing.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bonuses")
@RequiredArgsConstructor
public class BonusController {

    private final BonusService bonusService;

    @GetMapping("/me/balance")
    public ApiResponse<BonusBalanceResponse> getMyBalance() {
        return ApiResponse.<BonusBalanceResponse>builder()
                .success(true)
                .message("Бонусний баланс отримано")
                .data(bonusService.getMyBalance())
                .build();
    }

    @GetMapping("/me/history")
    public ApiResponse<List<BonusTransactionResponse>> getMyHistory() {
        return ApiResponse.<List<BonusTransactionResponse>>builder()
                .success(true)
                .message("Історію бонусів отримано")
                .data(bonusService.getMyHistory())
                .build();
    }
}