package com.carsharing.car.service.impl;

import com.carsharing.car.dto.CarImageResponse;
import com.carsharing.car.dto.CarRequest;
import com.carsharing.car.entity.CarLocation;
import com.carsharing.car.repository.CarLocationRepository;
import java.time.LocalDateTime;
import com.carsharing.car.dto.CarCardResponse;
import com.carsharing.car.dto.CarDetailsResponse;
import com.carsharing.car.entity.Car;
import com.carsharing.car.entity.CarLocation;
import com.carsharing.car.repository.CarImageRepository;
import com.carsharing.car.repository.CarLocationRepository;
import com.carsharing.car.repository.CarRepository;
import com.carsharing.car.service.CarService;
import com.carsharing.common.exception.BadRequestException;
import com.carsharing.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.carsharing.car.entity.CarImage;
import com.carsharing.common.storage.FileStorageService;
import com.carsharing.common.storage.UploadedFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarImageRepository imageRepository;
    private final CarLocationRepository carLocationRepository;
    private final FileStorageService fileStorageService;

    private CarDetailsResponse mapToDetails(Car car) {
        List<String> images = imageRepository.findByCarId(car.getId())
                .stream()
                .map(i -> i.getImageUrl())
                .toList();

        return CarDetailsResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .year(car.getYear())
                .color(car.getColor())
                .pricePerHour(car.getPricePerHour())
                .pricePerDay(car.getPricePerDay())
                .pricePerMonth(car.getPricePerMonth())
                .address(car.getLocation().getAddress())
                .latitude(car.getLocation().getLatitude().doubleValue())
                .longitude(car.getLocation().getLongitude().doubleValue())
                .images(images)
                .build();
    }

    @Override
    public void uploadCarImage(Long carId, MultipartFile image, Boolean isMain) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car not found"));

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Фото авто є обов'язковим");
        }

        boolean makeMain = Boolean.TRUE.equals(isMain)
                || !imageRepository.existsByCarIdAndIsMainTrue(car.getId());

        if (makeMain) {
            imageRepository.findByCarId(car.getId()).forEach(existing -> {
                existing.setIsMain(false);
                imageRepository.save(existing);
            });
        }

        UploadedFileResponse uploaded = fileStorageService.upload(image, "cars");

        CarImage carImage = CarImage.builder()
                .carId(car.getId())
                .imageUrl(uploaded.getUrl())
                .imagePublicId(uploaded.getPublicId())
                .isMain(makeMain)
                .createdAt(LocalDateTime.now())
                .build();

        imageRepository.save(carImage);
    }

    @Override
    public List<CarImageResponse> getCarImages(Long carId) {
        carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car not found"));

        return imageRepository.findByCarId(carId)
                .stream()
                .map(image -> CarImageResponse.builder()
                        .id(image.getId())
                        .carId(image.getCarId())
                        .imageUrl(image.getImageUrl())
                        .imagePublicId(image.getImagePublicId())
                        .isMain(image.getIsMain())
                        .build())
                .toList();
    }

    @Override
    public void deleteCarImage(Long imageId) {
        CarImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Image not found"));

        fileStorageService.delete(image.getImagePublicId());
        imageRepository.delete(image);
    }

    @Override
    public void setMainImage(Long imageId) {
        CarImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Image not found"));

        imageRepository.findByCarId(image.getCarId()).forEach(existing -> {
            existing.setIsMain(existing.getId().equals(imageId));
            imageRepository.save(existing);
        });
    }

    @Override
    public CarDetailsResponse createCar(CarRequest request) {
        CarLocation location = CarLocation.builder()
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        carLocationRepository.save(location);

        Car car = Car.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .registrationNumber(request.getRegistrationNumber())
                .color(request.getColor())
                .pricePerHour(request.getPricePerHour())
                .pricePerDay(request.getPricePerDay())
                .pricePerMonth(request.getPricePerMonth())
                .status(request.getStatus())
                .location(location)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Car savedCar = carRepository.save(car);

        return mapToDetails(savedCar);
    }

    @Override
    public CarDetailsResponse updateCar(Long id, CarRequest request) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Car not found"));

        CarLocation location = car.getLocation();

        location.setAddress(request.getAddress());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());

        carLocationRepository.save(location);

        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setRegistrationNumber(request.getRegistrationNumber());
        car.setColor(request.getColor());
        car.setPricePerHour(request.getPricePerHour());
        car.setPricePerDay(request.getPricePerDay());
        car.setPricePerMonth(request.getPricePerMonth());
        car.setStatus(request.getStatus());
        car.setUpdatedAt(LocalDateTime.now());

        Car savedCar = carRepository.save(car);

        return mapToDetails(savedCar);
    }

    @Override
    public List<CarCardResponse> getAllCars() {
        return carRepository.findAll().stream()
                .map(car -> CarCardResponse.builder()
                        .id(car.getId())
                        .brand(car.getBrand())
                        .model(car.getModel())
                        .city("TODO")
                        .address(car.getLocation().getAddress())
                        .pricePerHour(car.getPricePerHour())
                        .pricePerDay(car.getPricePerDay())
                        .pricePerMonth(car.getPricePerMonth())
                        .status(car.getStatus().name())
                        .imageUrl(getMainImage(car.getId()))
                        .year(car.getYear())
                        .registrationNumber(car.getRegistrationNumber())
                        .color(car.getColor())
                        .latitude(car.getLocation().getLatitude().doubleValue())
                        .longitude(car.getLocation().getLongitude().doubleValue())
                        .build())
                .toList();
    }

    @Override
    public CarDetailsResponse getById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Car not found"));

        return mapToDetails(car);
    }

    private String getMainImage(Long carId) {
        return imageRepository.findByCarId(carId).stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsMain()))
                .findFirst()
                .map(i -> i.getImageUrl())
                .orElse(null);
    }
}