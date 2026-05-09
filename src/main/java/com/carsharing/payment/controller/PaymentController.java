package com.carsharing.payment.controller;

import com.carsharing.common.response.ApiResponse;
import com.carsharing.payment.dto.LiqPayCheckoutResponse;
import com.carsharing.payment.dto.PaymentResponse;
import com.carsharing.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/api/rentals/{rentalId}/payments/mock")
    public ApiResponse<PaymentResponse> createMockPayment(@PathVariable Long rentalId) {
        return ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Mock платіж створено")
                .data(paymentService.createMockPayment(rentalId))
                .build();
    }

    @PostMapping("/api/payments/{paymentId}/mock-success")
    public ApiResponse<PaymentResponse> mockPay(@PathVariable Long paymentId) {
        return ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Mock оплата успішна")
                .data(paymentService.mockPay(paymentId))
                .build();
    }

    @GetMapping("/api/rentals/{rentalId}/payments")
    public ApiResponse<List<PaymentResponse>> getRentalPayments(@PathVariable Long rentalId) {
        return ApiResponse.<List<PaymentResponse>>builder()
                .success(true)
                .message("Платежі оренди отримано")
                .data(paymentService.getRentalPayments(rentalId))
                .build();
    }

    @PostMapping("/api/rentals/{rentalId}/payments/liqpay")
    public ApiResponse<LiqPayCheckoutResponse> createLiqPayPayment(@PathVariable Long rentalId) {
        return ApiResponse.<LiqPayCheckoutResponse>builder()
                .success(true)
                .message("LiqPay платіж створено")
                .data(paymentService.createLiqPayPayment(rentalId))
                .build();
    }

    @PostMapping("/api/payments/liqpay/callback")
    public String liqPayCallback(
            @RequestParam String data,
            @RequestParam String signature
    ) {
        paymentService.handleLiqPayCallback(data, signature);
        return "OK";
    }
}