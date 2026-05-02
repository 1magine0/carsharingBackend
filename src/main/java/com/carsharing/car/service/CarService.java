package com.carsharing.car.service;

import com.carsharing.car.dto.CarCardResponse;
import com.carsharing.car.dto.CarDetailsResponse;
import com.carsharing.car.dto.CarImageResponse;
import com.carsharing.car.dto.CarRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarService {

    List<CarCardResponse> getAllCars();

    CarDetailsResponse getById(Long id);

    CarDetailsResponse createCar(CarRequest request);

    CarDetailsResponse updateCar(Long id, CarRequest request);
    void uploadCarImage(Long carId, MultipartFile image, Boolean isMain);
    void deleteCarImage(Long imageId);
    void setMainImage(Long imageId);

    List<CarImageResponse> getCarImages(Long carId);
}