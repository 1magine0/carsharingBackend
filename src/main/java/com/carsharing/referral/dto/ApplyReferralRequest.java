package com.carsharing.referral.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyReferralRequest {

    @NotBlank(message = "Referral code є обов'язковим")
    private String referralCode;
}