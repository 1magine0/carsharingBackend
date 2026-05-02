package com.carsharing.rental.controller;

import com.carsharing.common.response.ApiResponse;
import com.carsharing.rental.dto.CreateRentalRequest;
import com.carsharing.rental.dto.RentalPreviewRequest;
import com.carsharing.rental.dto.RentalPreviewResponse;
import com.carsharing.rental.dto.RentalResponse;
import com.carsharing.rental.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/preview")
    public ApiResponse<RentalPreviewResponse> previewRental(@Valid @RequestBody RentalPreviewRequest request) {
        return ApiResponse.<RentalPreviewResponse>builder()
                .success(true)
                .message("Прев'ю оренди розраховано")
                .data(rentalService.previewRental(request))
                .build();
    }

    @PostMapping
    public ApiResponse<Void> createRental(@Valid @RequestBody CreateRentalRequest request) {
        rentalService.createRental(request);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Оренду створено успішно")
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<List<RentalResponse>> getMyRentals() {
        return ApiResponse.<List<RentalResponse>>builder()
                .success(true)
                .message("Список оренд отримано")
                .data(rentalService.getCurrentUserRentals())
                .build();
    }

    @GetMapping("/my/active")
    public ApiResponse<RentalResponse> getMyActiveRental() {
        return ApiResponse.<RentalResponse>builder()
                .success(true)
                .message("Активну оренду отримано")
                .data(rentalService.getCurrentUserActiveRental())
                .build();
    }

    @PostMapping("/{id}/finish")
    public ApiResponse<Void> finishRental(@PathVariable Long id) {
        rentalService.finishRental(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Оренду завершено")
                .build();
    }
}