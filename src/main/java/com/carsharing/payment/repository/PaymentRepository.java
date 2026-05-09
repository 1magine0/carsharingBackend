package com.carsharing.payment.repository;

import com.carsharing.payment.entity.Payment;
import com.carsharing.payment.entity.PaymentStatus;
import com.carsharing.rental.entity.Rental;
import com.carsharing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findByRentalOrderByCreatedAtDesc(Rental rental);

    Optional<Payment> findFirstByRentalAndStatusOrderByCreatedAtDesc(
            Rental rental,
            PaymentStatus status
    );

    Optional<Payment> findByIdAndUser(Long id, User user);

}