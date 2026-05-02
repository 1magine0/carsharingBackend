package com.carsharing.admin.controller;

import com.carsharing.common.response.ApiResponse;
import com.carsharing.license.dto.LicenseResponse;
import com.carsharing.license.service.DriverLicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/licenses")
@RequiredArgsConstructor
public class AdminLicenseController {

    private final DriverLicenseService service;

    @GetMapping("/pending")
    public ApiResponse<List<LicenseResponse>> getPending() {
        return ApiResponse.<List<LicenseResponse>>builder()
                .success(true)
                .data(service.getPending())
                .build();
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id) {
        service.approve(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Approved")
                .build();
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id,
                                    @RequestParam String reason) {
        service.reject(id, reason);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Rejected")
                .build();
    }
}