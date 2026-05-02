package com.carsharing.bonus.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusBalanceResponse {
    private BigDecimal balance;
}