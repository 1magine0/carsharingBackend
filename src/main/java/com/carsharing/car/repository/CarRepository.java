package com.carsharing.car.repository;

import com.carsharing.car.entity.Car;
import com.carsharing.car.entity.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByStatus(CarStatus status);
}