package com.carsharing.admin.controller;

import com.carsharing.car.dto.CarDetailsResponse;
import com.carsharing.car.dto.CarImageResponse;
import com.carsharing.car.dto.CarRequest;
import com.carsharing.car.service.CarService;
import com.carsharing.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cars")
@RequiredArgsConstructor
public class AdminCarController {

    private final CarService carService;

    @PostMapping
    public ApiResponse<CarDetailsResponse> createCar(@Valid @RequestBody CarRequest request) {
        return ApiResponse.<CarDetailsResponse>builder()
                .success(true)
                .message("Авто створено")
                .data(carService.createCar(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CarDetailsResponse> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarRequest request
    ) {
        return ApiResponse.<CarDetailsResponse>builder()
                .success(true)
                .message("Авто оновлено")
                .data(carService.updateCar(id, request))
                .build();
    }

    @GetMapping("/{id}/images")
    public ApiResponse<List<CarImageResponse>> getCarImages(@PathVariable Long id) {
        return ApiResponse.<List<CarImageResponse>>builder()
                .success(true)
                .message("Фото авто отримано")
                .data(carService.getCarImages(id))
                .build();
    }

    @PostMapping(value = "/{id}/images", consumes = "multipart/form-data")
    public ApiResponse<Void> uploadCarImage(
            @PathVariable Long id,
            @RequestParam MultipartFile image,
            @RequestParam(defaultValue = "false") Boolean isMain
    ) {
        carService.uploadCarImage(id, image, isMain);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Фото авто завантажено")
                .build();
    }

    @PostMapping("/images/{imageId}/main")
    public ApiResponse<Void> setMainImage(@PathVariable Long imageId) {
        carService.setMainImage(imageId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Головне фото оновлено")
                .build();
    }

    @DeleteMapping("/images/{imageId}")
    public ApiResponse<Void> deleteCarImage(@PathVariable Long imageId) {
        carService.deleteCarImage(imageId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Фото авто видалено")
                .build();
    }
}