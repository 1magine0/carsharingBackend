package com.carsharing.rental.repository;

import com.carsharing.rental.entity.Rental;
import com.carsharing.rental.entity.RentalStatus;
import com.carsharing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUser(User user);

    boolean existsByUserAndStatus(User user, RentalStatus status);

    Optional<Rental> findByIdAndUser(Long id, User user);

    List<Rental> findByStatus(RentalStatus status);

    List<Rental> findByStatusAndEndTimeBefore(RentalStatus status, LocalDateTime endTime);

    long countByUserAndStatus(User user, RentalStatus status);
}