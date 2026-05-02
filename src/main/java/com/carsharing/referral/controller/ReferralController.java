package com.carsharing.referral.controller;

import com.carsharing.common.response.ApiResponse;
import com.carsharing.referral.dto.ApplyReferralRequest;
import com.carsharing.referral.dto.ReferralInfoResponse;
import com.carsharing.referral.service.ReferralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    @PostMapping("/apply")
    public ApiResponse<Void> applyReferralCode(@Valid @RequestBody ApplyReferralRequest request) {
        referralService.applyReferralCode(request);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Referral code застосовано")
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<ReferralInfoResponse> getMyReferralInfo() {
        return ApiResponse.<ReferralInfoResponse>builder()
                .success(true)
                .message("Referral info отримано")
                .data(referralService.getMyReferralInfo())
                .build();
    }
}