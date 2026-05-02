package com.carsharing.bonus.dto;

import com.carsharing.bonus.entity.BonusOperationType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusTransactionResponse {

    private Long id;
    private BigDecimal amount;
    private BonusOperationType operationType;
    private String description;
    private LocalDateTime createdAt;
    private Long rentalId;
}