package com.carsharing.referral.entity;

import com.carsharing.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "referrer_user_id", nullable = false)
    private User referrerUser;

    @OneToOne(optional = false)
    @JoinColumn(name = "referred_user_id", nullable = false, unique = true)
    private User referredUser;

    @Column(name = "referral_bonus_granted", nullable = false)
    private Boolean referralBonusGranted;

    @Column(name = "referred_discount_granted", nullable = false)
    private Boolean referredDiscountGranted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}