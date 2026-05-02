package com.carsharing.admin.controller;

import com.carsharing.admin.dto.AdminRentalResponse;
import com.carsharing.admin.service.AdminRentalService;
import com.carsharing.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.carsharing.rental.dto.RentalPhotoResponse;
import com.carsharing.rental.entity.RentalPhotoType;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rentals")
@RequiredArgsConstructor
public class AdminRentalController {

    private final AdminRentalService adminRentalService;

    @GetMapping
    public ApiResponse<List<AdminRentalResponse>> getAllRentals() {
        return ApiResponse.<List<AdminRentalResponse>>builder()
                .success(true)
                .message("Список оренд отримано")
                .data(adminRentalService.getAllRentals())
                .build();
    }

    @GetMapping("/active")
    public ApiResponse<List<AdminRentalResponse>> getActiveRentals() {
        return ApiResponse.<List<AdminRentalResponse>>builder()
                .success(true)
                .message("Список активних оренд отримано")
                .data(adminRentalService.getActiveRentals())
                .build();
    }

    @GetMapping("/{rentalId}/photos")
    public ApiResponse<List<RentalPhotoResponse>> getRentalPhotos(
            @PathVariable Long rentalId
    ) {
        return ApiResponse.<List<RentalPhotoResponse>>builder()
                .success(true)
                .message("Фото оренди отримано")
                .data(adminRentalService.getRentalPhotos(rentalId))
                .build();
    }

    @GetMapping("/{rentalId}/photos/{photoType}")
    public ApiResponse<List<RentalPhotoResponse>> getRentalPhotosByType(
            @PathVariable Long rentalId,
            @PathVariable RentalPhotoType photoType
    ) {
        return ApiResponse.<List<RentalPhotoResponse>>builder()
                .success(true)
                .message("Фото оренди отримано")
                .data(adminRentalService.getRentalPhotosByType(rentalId, photoType))
                .build();
    }
}