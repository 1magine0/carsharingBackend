package com.carsharing.car.repository;

import com.carsharing.car.entity.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarImageRepository extends JpaRepository<CarImage, Long> {

    List<CarImage> findByCarId(Long carId);

    boolean existsByCarIdAndIsMainTrue(Long carId);
}