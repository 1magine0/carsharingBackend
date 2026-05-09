package com.carsharing.rental.scheduler;

import com.carsharing.car.entity.Car;
import com.carsharing.car.entity.CarStatus;
import com.carsharing.car.repository.CarRepository;
import com.carsharing.rental.entity.Rental;
import com.carsharing.rental.entity.RentalStatus;
import com.carsharing.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RentalExpirationScheduler {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;

    @Scheduled(fixedRate = 60000)
    public void expireOverdueRentals() {
        LocalDateTime now = LocalDateTime.now();

        List<Rental> overdueRentals = rentalRepository.findByStatusAndEndTimeBefore(
                RentalStatus.ACTIVE,
                now
        );

        for (Rental rental : overdueRentals) {
            rental.setStatus(RentalStatus.EXPIRED);
            rental.setUpdatedAt(now);
            rentalRepository.save(rental);

            Car car = rental.getCar();
            car.setStatus(CarStatus.SERVICE);
            car.setUpdatedAt(now);
            carRepository.save(car);

            System.out.println(
                    "Rental expired automatically: rentalId=" + rental.getId()
                            + ", carId=" + car.getId()
            );
        }
    }
}