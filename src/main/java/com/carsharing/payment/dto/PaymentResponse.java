package com.carsharing.payment.dto;

import com.carsharing.payment.entity.PaymentProvider;
import com.carsharing.payment.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private Long rentalId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentProvider provider;
    private String orderId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}