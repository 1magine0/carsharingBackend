package com.carsharing.admin.dto;

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
public class AdminRentalResponse {

    private Long id;

    private Long userId;
    private String userFullName;
    private String userEmail;
    private String userPhone;

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

    private Long beforePhotoCount;
    private Long afterPhotoCount;
}