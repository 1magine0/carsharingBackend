package com.carsharing.auth.controller;

import com.carsharing.auth.dto.AuthResponse;
import com.carsharing.auth.dto.LoginRequest;
import com.carsharing.auth.dto.RegisterRequest;
import com.carsharing.auth.service.AuthService;
import com.carsharing.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Реєстрація успішна")
                .data(null)
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Вхід виконано успішно")
                .data(response)
                .build();
    }
}