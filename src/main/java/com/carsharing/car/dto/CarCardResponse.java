package com.carsharing.car.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarCardResponse {

    private Long id;
    private String brand;
    private String model;
    private Short year;
    private String registrationNumber;
    private String color;

    private String city;
    private String address;
    private Double latitude;
    private Double longitude;

    private BigDecimal pricePerHour;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerMonth;

    private String status;
    private String imageUrl;
}