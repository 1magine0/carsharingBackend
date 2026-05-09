package com.carsharing.rental.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnlockCarResponse {

    private Boolean unlockAllowed;
    private Long rentalId;
    private Long carId;
    private String carName;
    private String message;
}