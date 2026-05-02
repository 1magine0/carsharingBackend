package com.carsharing.referral.service.impl;

import com.carsharing.common.exception.BadRequestException;
import com.carsharing.referral.dto.ApplyReferralRequest;
import com.carsharing.referral.dto.ReferralInfoResponse;
import com.carsharing.referral.entity.Referral;
import com.carsharing.referral.repository.ReferralRepository;
import com.carsharing.referral.service.ReferralService;
import com.carsharing.user.entity.User;
import com.carsharing.user.repository.UserRepository;
import com.carsharing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {

    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void applyReferralCode(ApplyReferralRequest request) {
        User currentUser = userService.getCurrentUserEntity();

        if (referralRepository.existsByReferredUser(currentUser)) {
            throw new BadRequestException("Для цього користувача referral уже застосований");
        }

        User referrer = userRepository.findByReferralCode(request.getReferralCode())
                .orElseThrow(() -> new BadRequestException("Referral code не знайдено"));

        if (referrer.getId().equals(currentUser.getId())) {
            throw new BadRequestException("Користувач не може застосувати власний referral code");
        }

        Referral referral = Referral.builder()
                .referrerUser(referrer)
                .referredUser(currentUser)
                .referralBonusGranted(false)
                .referredDiscountGranted(false)
                .createdAt(LocalDateTime.now())
                .build();

        referralRepository.save(referral);
    }

    @Override
    public ReferralInfoResponse getMyReferralInfo() {
        User currentUser = userService.getCurrentUserEntity();

        ReferralInfoResponse.ReferralInfoResponseBuilder builder = ReferralInfoResponse.builder()
                .myReferralCode(currentUser.getReferralCode());

        referralRepository.findByReferredUser(currentUser).ifPresent(referral -> {
            builder.referralId(referral.getId());
            builder.referrerUserId(referral.getReferrerUser().getId());
            builder.referrerEmail(referral.getReferrerUser().getEmail());
            builder.referralBonusGranted(referral.getReferralBonusGranted());
            builder.referredDiscountGranted(referral.getReferredDiscountGranted());
        });

        return builder.build();
    }
}