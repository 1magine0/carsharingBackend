package com.carsharing.rental.dto;

import com.carsharing.rental.entity.TariffType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalPreviewRequest {

    @NotNull(message = "Потрібно вказати автомобіль")
    private Long carId;

    @NotNull(message = "Потрібно вибрати тариф")
    private TariffType tariffType;

    @NotNull(message = "Потрібно вказати час початку")
    private LocalDateTime startTime;

    @NotNull(message = "Потрібно вказати час завершення")
    private LocalDateTime endTime;
}