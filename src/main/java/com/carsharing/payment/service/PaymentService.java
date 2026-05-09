package com.carsharing.payment.service;

import com.carsharing.payment.dto.LiqPayCheckoutResponse;
import com.carsharing.payment.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createMockPayment(Long rentalId);

    PaymentResponse mockPay(Long paymentId);

    LiqPayCheckoutResponse createLiqPayPayment(Long rentalId);

    List<PaymentResponse> getRentalPayments(Long rentalId);

    void handleLiqPayCallback(String data, String signature);
}