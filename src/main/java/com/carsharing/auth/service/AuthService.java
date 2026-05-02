package com.carsharing.auth.service;

import com.carsharing.auth.dto.AuthResponse;
import com.carsharing.auth.dto.LoginRequest;
import com.carsharing.auth.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}