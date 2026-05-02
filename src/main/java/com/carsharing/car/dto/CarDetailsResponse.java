package com.carsharing.car.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDetailsResponse {

    private Long id;
    private String brand;
    private String model;
    private Short year;
    private String color;

    private BigDecimal pricePerHour;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerMonth;

    private String address;
    private Double latitude;
    private Double longitude;

    private List<String> images;
}