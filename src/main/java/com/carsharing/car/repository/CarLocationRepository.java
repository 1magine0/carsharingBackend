package com.carsharing.car.repository;

import com.carsharing.car.entity.CarLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarLocationRepository extends JpaRepository<CarLocation, Long> {
}