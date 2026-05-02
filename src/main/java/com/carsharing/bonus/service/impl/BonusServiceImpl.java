package com.carsharing.bonus.service.impl;

import com.carsharing.bonus.dto.BonusBalanceResponse;
import com.carsharing.bonus.dto.BonusTransactionResponse;
import com.carsharing.bonus.entity.BonusOperationType;
import com.carsharing.bonus.entity.BonusTransaction;
import com.carsharing.bonus.repository.BonusTransactionRepository;
import com.carsharing.bonus.service.BonusService;
import com.carsharing.user.entity.User;
import com.carsharing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BonusServiceImpl implements BonusService {

    private final BonusTransactionRepository bonusTransactionRepository;
    private final UserService userService;

    @Override
    public BonusBalanceResponse getMyBalance() {
        User currentUser = userService.getCurrentUserEntity();

        List<BonusTransaction> transactions = bonusTransactionRepository.findByUserOrderByCreatedAtDesc(currentUser);

        BigDecimal balance = transactions.stream()
                .map(this::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BonusBalanceResponse.builder()
                .balance(balance)
                .build();
    }

    @Override
    public List<BonusTransactionResponse> getMyHistory() {
        User currentUser = userService.getCurrentUserEntity();

        return bonusTransactionRepository.findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BigDecimal signedAmount(BonusTransaction transaction) {
        return switch (transaction.getOperationType()) {
            case EARN, REFERRAL -> transaction.getAmount();
            case SPEND -> transaction.getAmount().negate();
        };
    }

    private BonusTransactionResponse mapToResponse(BonusTransaction transaction) {
        return BonusTransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .operationType(transaction.getOperationType())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .rentalId(transaction.getRental() != null ? transaction.getRental().getId() : null)
                .build();
    }
}