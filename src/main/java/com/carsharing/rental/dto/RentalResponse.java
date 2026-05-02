package com.carsharing.rental.dto;

import com.carsharing.rental.entity.RentalStatus;
import com.carsharing.rental.entity.TariffType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalResponse {

    private Long id;

    private Long carId;
    private String carBrand;
    private String carModel;
    private String carRegistrationNumber;

    private TariffType tariffType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private BigDecimal totalPrice;
    private BigDecimal bonusUsed;
    private BigDecimal discountAmount;

    private RentalStatus status;
}