package com.carsharing.referral.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralStatsResponse {

    private String referralCode;
    private Long invitedUsersCount;
    private Long rewardedReferralsCount;
}