package com.carsharing.referral.controller;

import com.carsharing.common.response.ApiResponse;
import com.carsharing.referral.dto.ReferralStatsResponse;
import com.carsharing.referral.service.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    @GetMapping("/validate")
    public ApiResponse<Boolean> validateReferralCode(@RequestParam String code) {
        boolean isValid = referralService.isReferralCodeValid(code);

        return ApiResponse.<Boolean>builder()
                .success(true)
                .message(isValid ? "Реферальний код дійсний" : "Реферальний код не знайдено")
                .data(isValid)
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<ReferralStatsResponse> getMyReferralStats() {
        return ApiResponse.<ReferralStatsResponse>builder()
                .success(true)
                .message("Реферальну статистику отримано")
                .data(referralService.getMyReferralStats())
                .build();
    }
}