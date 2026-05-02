package com.carsharing.car.controller;

import com.carsharing.car.dto.CarCardResponse;
import com.carsharing.car.dto.CarDetailsResponse;
import com.carsharing.car.service.CarService;
import com.carsharing.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService service;

    @GetMapping
    public ApiResponse<List<CarCardResponse>> getAll() {
        return ApiResponse.<List<CarCardResponse>>builder()
                .success(true)
                .data(service.getAllCars())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CarDetailsResponse> getById(@PathVariable Long id) {
        return ApiResponse.<CarDetailsResponse>builder()
                .success(true)
                .data(service.getById(id))
                .build();
    }
}