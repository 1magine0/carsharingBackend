package com.carsharing.referral.service.impl;

import com.carsharing.bonus.entity.BonusTransaction;
import com.carsharing.bonus.entity.BonusOperationType;
import com.carsharing.bonus.repository.BonusTransactionRepository;
import com.carsharing.common.exception.BadRequestException;
import com.carsharing.referral.dto.ReferralStatsResponse;
import com.carsharing.referral.entity.Referral;
import com.carsharing.referral.repository.ReferralRepository;
import com.carsharing.referral.service.ReferralService;
import com.carsharing.rental.entity.RentalStatus;
import com.carsharing.rental.repository.RentalRepository;
import com.carsharing.user.entity.User;
import com.carsharing.user.repository.UserRepository;
import com.carsharing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {

    private static final BigDecimal REFERRED_USER_START_BONUS = BigDecimal.valueOf(100);
    private static final BigDecimal REFERRER_BONUS = BigDecimal.valueOf(200);

    private final UserRepository userRepository;
    private final ReferralRepository referralRepository;
    private final BonusTransactionRepository bonusTransactionRepository;
    private final RentalRepository rentalRepository;
    private final UserService userService;

    @Override
    public boolean isReferralCodeValid(String referralCode) {
        if (referralCode == null || referralCode.isBlank()) {
            return false;
        }

        String normalizedCode = referralCode.trim().toUpperCase();

        return userRepository.findByReferralCode(normalizedCode).isPresent();
    }

    @Override
    public ReferralStatsResponse getMyReferralStats() {
        User currentUser = userService.getCurrentUserEntity();

        long invitedUsersCount = referralRepository.countByReferrerUser(currentUser);
        long rewardedReferralsCount =
                referralRepository.countByReferrerUserAndReferralBonusGrantedTrue(currentUser);

        return ReferralStatsResponse.builder()
                .referralCode(currentUser.getReferralCode())
                .invitedUsersCount(invitedUsersCount)
                .rewardedReferralsCount(rewardedReferralsCount)
                .build();
    }

    @Override
    public void handleReferralOnRegistration(User newUser, String referralCode) {
        if (referralCode == null || referralCode.isBlank()) {
            return;
        }

        String normalizedCode = referralCode.trim().toUpperCase();

        User referrer = userRepository.findByReferralCode(normalizedCode)
                .orElseThrow(() -> new BadRequestException("Реферальний код не знайдено"));

        if (referrer.getId().equals(newUser.getId())) {
            throw new BadRequestException("Не можна використати власний реферальний код");
        }

        if (referralRepository.existsByReferredUser(newUser)) {
            throw new BadRequestException("Для цього користувача реферальний код уже використано");
        }

        Referral referral = Referral.builder()
                .referrerUser(referrer)
                .referredUser(newUser)
                .referralBonusGranted(false)
                .referredDiscountGranted(true)
                .createdAt(LocalDateTime.now())
                .build();

        referralRepository.save(referral);

        BonusTransaction bonus = BonusTransaction.builder()
                .user(newUser)
                .amount(REFERRED_USER_START_BONUS)
                .operationType(BonusOperationType.EARN)
                .description("Стартовий бонус за реєстрацію за реферальним кодом")
                .createdAt(LocalDateTime.now())
                .build();

        bonusTransactionRepository.save(bonus);
    }

    @Override
    public void handleReferralBonusAfterFirstFinishedRental(User referredUser) {
        Referral referral = referralRepository.findByReferredUser(referredUser)
                .orElse(null);

        if (referral == null) {
            return;
        }

        if (Boolean.TRUE.equals(referral.getReferralBonusGranted())) {
            return;
        }

        long finishedRentalsCount = rentalRepository.countByUserAndStatus(
                referredUser,
                RentalStatus.FINISHED
        );

        if (finishedRentalsCount < 1) {
            return;
        }

        BonusTransaction bonus = BonusTransaction.builder()
                .user(referral.getReferrerUser())
                .amount(REFERRER_BONUS)
                .operationType(BonusOperationType.EARN)
                .description("Бонус за першу завершену оренду запрошеного користувача")
                .createdAt(LocalDateTime.now())
                .build();

        bonusTransactionRepository.save(bonus);

        referral.setReferralBonusGranted(true);
        referralRepository.save(referral);
    }
}