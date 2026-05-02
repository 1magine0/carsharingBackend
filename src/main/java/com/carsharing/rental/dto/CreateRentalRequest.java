package com.carsharing.rental.dto;

import com.carsharing.rental.entity.TariffType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRentalRequest {

    @NotNull(message = "Потрібно вказати автомобіль")
    private Long carId;

    @NotNull(message = "Потрібно вибрати тариф")
    private TariffType tariffType;

    @NotNull(message = "Потрібно вказати час початку")
    private LocalDateTime startTime;

    @NotNull(message = "Потрібно вказати час завершення")
    private LocalDateTime endTime;

    @NotNull(message = "Потрібно вказати кількість бонусів")
    @DecimalMin(value = "0.00", message = "Бонуси не можуть бути від'ємними")
    private BigDecimal bonusUsed;
}