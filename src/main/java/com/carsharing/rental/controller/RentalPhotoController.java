package com.carsharing.rental.controller;

import com.carsharing.common.response.ApiResponse;
import com.carsharing.rental.dto.RentalPhotoResponse;
import com.carsharing.rental.entity.RentalPhotoType;
import com.carsharing.rental.service.RentalPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalPhotoController {

    private final RentalPhotoService rentalPhotoService;

    @PostMapping(value = "/{rentalId}/photos", consumes = "multipart/form-data")
    public ApiResponse<RentalPhotoResponse> uploadPhoto(
            @PathVariable Long rentalId,
            @RequestParam RentalPhotoType photoType,
            @RequestParam MultipartFile image
    ) {
        return ApiResponse.<RentalPhotoResponse>builder()
                .success(true)
                .message("Фото оренди завантажено")
                .data(rentalPhotoService.uploadPhoto(rentalId, photoType, image))
                .build();
    }

    @GetMapping("/{rentalId}/photos")
    public ApiResponse<List<RentalPhotoResponse>> getPhotos(@PathVariable Long rentalId) {
        return ApiResponse.<List<RentalPhotoResponse>>builder()
                .success(true)
                .message("Фото оренди отримано")
                .data(rentalPhotoService.getRentalPhotos(rentalId))
                .build();
    }

    @GetMapping("/{rentalId}/photos/{photoType}")
    public ApiResponse<List<RentalPhotoResponse>> getPhotosByType(
            @PathVariable Long rentalId,
            @PathVariable RentalPhotoType photoType
    ) {
        return ApiResponse.<List<RentalPhotoResponse>>builder()
                .success(true)
                .message("Фото оренди отримано")
                .data(rentalPhotoService.getRentalPhotosByType(rentalId, photoType))
                .build();
    }

    @DeleteMapping("/photos/{photoId}")
    public ApiResponse<Void> deletePhoto(@PathVariable Long photoId) {
        rentalPhotoService.deletePhoto(photoId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Фото видалено")
                .build();
    }
}