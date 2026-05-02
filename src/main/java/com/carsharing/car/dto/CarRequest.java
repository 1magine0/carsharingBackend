package com.carsharing.car.dto;

import com.carsharing.car.entity.CarStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarRequest {

    @NotBlank(message = "Brand is required")
    @Size(max = 80)
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 80)
    private String model;

    @NotNull(message = "Year is required")
    @Min(1990)
    @Max(2100)
    private Short year;

    @NotBlank(message = "Registration number is required")
    @Size(max = 20)
    private String registrationNumber;

    @NotBlank(message = "Color is required")
    @Size(max = 40)
    private String color;

    @NotNull(message = "Price per hour is required")
    @DecimalMin(value = "0.01")
    private BigDecimal pricePerHour;

    @NotNull(message = "Price per day is required")
    @DecimalMin(value = "0.01")
    private BigDecimal pricePerDay;

    @NotNull(message = "Price per month is required")
    @DecimalMin(value = "0.01")
    private BigDecimal pricePerMonth;

    @NotNull(message = "Status is required")
    private CarStatus status;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;
}