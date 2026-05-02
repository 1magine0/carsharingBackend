package com.carsharing.rental.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalPreviewResponse {

    private BigDecimal basePrice;
    private BigDecimal availableBonusBalance;
    private BigDecimal maxBonusUsage;
    private BigDecimal finalPrice;
}