package com.carsharing.car.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarImageResponse {
    private Long id;
    private Long carId;
    private String imageUrl;
    private String imagePublicId;
    private Boolean isMain;
}