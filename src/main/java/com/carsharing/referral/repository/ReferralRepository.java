package com.carsharing.referral.repository;

import com.carsharing.referral.entity.Referral;
import com.carsharing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferralRepository extends JpaRepository<Referral, Long> {

    Optional<Referral> findByReferredUser(User referredUser);

    boolean existsByReferredUser(User referredUser);

    long countByReferrerUser(User referrerUser);

    long countByReferrerUserAndReferralBonusGrantedTrue(User referrerUser);

}