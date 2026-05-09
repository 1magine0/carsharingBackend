package com.carsharing.payment.service.impl;

import com.carsharing.car.entity.Car;
import com.carsharing.car.entity.CarStatus;
import com.carsharing.car.repository.CarRepository;
import com.carsharing.common.exception.BadRequestException;
import com.carsharing.common.exception.NotFoundException;
import com.carsharing.payment.dto.PaymentResponse;
import com.carsharing.payment.entity.Payment;
import com.carsharing.payment.entity.PaymentProvider;
import com.carsharing.payment.entity.PaymentStatus;
import com.carsharing.payment.repository.PaymentRepository;
import com.carsharing.payment.service.LiqPayService;
import com.carsharing.payment.service.PaymentService;
import com.carsharing.rental.entity.Rental;
import com.carsharing.rental.entity.RentalStatus;
import com.carsharing.rental.repository.RentalRepository;
import com.carsharing.user.entity.User;
import com.carsharing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.carsharing.payment.dto.LiqPayCheckoutResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final UserService userService;
    private final CarRepository carRepository;
    private final LiqPayService liqPayService;
    @Value("${liqpay.checkout-url}")
    private String checkoutUrl;

    @Override
    public PaymentResponse createMockPayment(Long rentalId) {
        User currentUser = userService.getCurrentUserEntity();

        Rental rental = rentalRepository.findByIdAndUser(rentalId, currentUser)
                .orElseThrow(() -> new NotFoundException("Оренду не знайдено"));

        if (rental.getStatus() != RentalStatus.BOOKED) {
            throw new BadRequestException("Створити платіж можна тільки для заброньованої оренди");
        }

        paymentRepository.findFirstByRentalAndStatusOrderByCreatedAtDesc(rental, PaymentStatus.PAID)
                .ifPresent(payment -> {
                    throw new BadRequestException("Оренда вже оплачена");
                });

        Payment payment = Payment.builder()
                .rental(rental)
                .user(currentUser)
                .amount(rental.getTotalPrice())
                .currency("UAH")
                .status(PaymentStatus.PENDING)
                .provider(PaymentProvider.MOCK)
                .orderId("mock_rental_" + rental.getId() + "_" + UUID.randomUUID())
                .description("Mock оплата оренди авто #" + rental.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return map(paymentRepository.save(payment));
    }
    @Override
    public void handleLiqPayCallback(String data, String signature) {
        if (!liqPayService.isSignatureValid(data, signature)) {
            throw new BadRequestException("Некоректний підпис LiqPay callback");
        }

        JsonNode json = liqPayService.decodeData(data);

        String orderId = json.path("order_id").asText();
        String status = json.path("status").asText();
        String providerPaymentId = json.path("payment_id").asText(null);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Платіж не знайдено"));

        if ("success".equals(status) || "sandbox".equals(status)) {
            markPaymentPaidAndActivateRental(payment, providerPaymentId);
            return;
        }

        if ("failure".equals(status) || "error".equals(status)) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setProviderPaymentId(providerPaymentId);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            return;
        }

        if ("reversed".equals(status) || "refund".equals(status)) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setProviderPaymentId(providerPaymentId);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            return;
        }

        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }
    @Override
    public LiqPayCheckoutResponse createLiqPayPayment(Long rentalId) {
        User currentUser = userService.getCurrentUserEntity();

        Rental rental = rentalRepository.findByIdAndUser(rentalId, currentUser)
                .orElseThrow(() -> new NotFoundException("Оренду не знайдено"));

        if (rental.getStatus() != RentalStatus.BOOKED) {
            throw new BadRequestException("Оплатити можна тільки заброньовану оренду");
        }

        paymentRepository.findFirstByRentalAndStatusOrderByCreatedAtDesc(rental, PaymentStatus.PAID)
                .ifPresent(payment -> {
                    throw new BadRequestException("Оренда вже оплачена");
                });

        Payment payment = Payment.builder()
                .rental(rental)
                .user(currentUser)
                .amount(rental.getTotalPrice())
                .currency("UAH")
                .status(PaymentStatus.PENDING)
                .provider(PaymentProvider.LIQPAY_SANDBOX)
                .orderId("liqpay_rental_" + rental.getId() + "_" + UUID.randomUUID())
                .description("Оплата оренди авто #" + rental.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        Map<String, Object> params = liqPayService.createCheckoutParams(
                savedPayment.getOrderId(),
                savedPayment.getAmount(),
                savedPayment.getDescription()
        );

        String data = liqPayService.createData(params);
        String signature = liqPayService.createSignature(data);

        return LiqPayCheckoutResponse.builder()
                .paymentId(savedPayment.getId())
                .rentalId(rental.getId())
                .checkoutUrl(checkoutUrl)
                .data(data)
                .signature(signature)
                .build();
    }

    @Override
    public PaymentResponse mockPay(Long paymentId) {
        User currentUser = userService.getCurrentUserEntity();

        Payment payment = paymentRepository.findByIdAndUser(paymentId, currentUser)
                .orElseThrow(() -> new NotFoundException("Платіж не знайдено"));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return map(payment);
        }

        markPaymentPaidAndActivateRental(payment, "mock-" + payment.getId());

        return map(payment);
    }

    @Override
    public List<PaymentResponse> getRentalPayments(Long rentalId) {
        User currentUser = userService.getCurrentUserEntity();

        Rental rental = rentalRepository.findByIdAndUser(rentalId, currentUser)
                .orElseThrow(() -> new NotFoundException("Оренду не знайдено"));

        return paymentRepository.findByRentalOrderByCreatedAtDesc(rental)
                .stream()
                .map(this::map)
                .toList();
    }

    private void markPaymentPaidAndActivateRental(Payment payment, String providerPaymentId) {
        Rental rental = payment.getRental();

        if (rental.getStatus() != RentalStatus.BOOKED) {
            throw new BadRequestException("Активувати можна тільки заброньовану оренду");
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setProviderPaymentId(providerPaymentId);
        payment.setPaidAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        rental.setStatus(RentalStatus.ACTIVE);
        rental.setUpdatedAt(LocalDateTime.now());
        rentalRepository.save(rental);

        Car car = rental.getCar();
        car.setStatus(CarStatus.RENTED);
        car.setUpdatedAt(LocalDateTime.now());
        carRepository.save(car);
    }

    private PaymentResponse map(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .rentalId(payment.getRental().getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .provider(payment.getProvider())
                .orderId(payment.getOrderId())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt())
                .build();
    }
}