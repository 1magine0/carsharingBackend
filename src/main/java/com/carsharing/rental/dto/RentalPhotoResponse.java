package com.carsharing.rental.dto;

import com.carsharing.rental.entity.RentalPhotoType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalPhotoResponse {

    private Long id;
    private Long rentalId;
    private RentalPhotoType photoType;
    private String imageUrl;
    private LocalDateTime uploadedAt;
}