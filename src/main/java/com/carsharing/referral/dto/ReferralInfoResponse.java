package com.carsharing.referral.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralInfoResponse {

    private String myReferralCode;

    private Long referralId;
    private Long referrerUserId;
    private String referrerEmail;

    private Boolean referralBonusGranted;
    private Boolean referredDiscountGranted;
}