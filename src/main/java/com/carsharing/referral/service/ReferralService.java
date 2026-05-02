package com.carsharing.referral.service;

import com.carsharing.referral.dto.ApplyReferralRequest;
import com.carsharing.referral.dto.ReferralInfoResponse;

public interface ReferralService {

    void applyReferralCode(ApplyReferralRequest request);

    ReferralInfoResponse getMyReferralInfo();
}