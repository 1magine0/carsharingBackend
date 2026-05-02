package com.carsharing.license.controller;

import com.carsharing.common.response.ApiResponse;
import com.carsharing.license.dto.LicenseResponse;
import com.carsharing.license.service.DriverLicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
public class DriverLicenseController {

    private final DriverLicenseService service;

    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<Void> upload(
            @RequestParam String documentNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @RequestParam MultipartFile image
    ) {
        service.upload(documentNumber, issueDate, expiryDate, image);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Посвідчення завантажено")
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<LicenseResponse> getMyLicense() {
        return ApiResponse.<LicenseResponse>builder()
                .success(true)
                .data(service.getMyLicense())
                .build();
    }
}