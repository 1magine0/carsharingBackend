package com.carsharing.common.controller;

import com.carsharing.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ApiResponse<String> health() {
        return ApiResponse.<String>builder()
                .success(true)
                .message("Backend is running")
                .data("OK")
                .build();
    }
}