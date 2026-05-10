package com.carsharing.referral.service;

import com.carsharing.referral.dto.ReferralStatsResponse;
import com.carsharing.user.entity.User;

public interface ReferralService {

    void handleReferralOnRegistration(User newUser, String referralCode);

    void handleReferralBonusAfterFirstFinishedRental(User referredUser);

    boolean isReferralCodeValid(String referralCode);

    ReferralStatsResponse getMyReferralStats();
}