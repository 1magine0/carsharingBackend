package com.carsharing.payment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiqPayCheckoutResponse {

    private Long paymentId;
    private Long rentalId;

    private String checkoutUrl;
    private String data;
    private String signature;
}