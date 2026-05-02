package com.carsharing.user.controller;

import com.carsharing.common.response.ApiResponse;
import com.carsharing.user.dto.UpdateProfileRequest;
import com.carsharing.user.dto.UserProfileResponse;
import com.carsharing.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getCurrentUser() {
        UserProfileResponse response = userService.getCurrentUser();

        return ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .message("Профіль користувача отримано")
                .data(response)
                .build();
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateCurrentUser(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserProfileResponse response = userService.updateCurrentUser(request);

        return ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .message("Профіль оновлено")
                .data(response)
                .build();
    }
}