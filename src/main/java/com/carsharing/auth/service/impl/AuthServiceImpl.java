package com.carsharing.auth.service.impl;

import com.carsharing.auth.dto.AuthResponse;
import com.carsharing.auth.dto.LoginRequest;
import com.carsharing.auth.dto.RegisterRequest;
import com.carsharing.auth.service.AuthService;
import com.carsharing.common.exception.BadRequestException;
import com.carsharing.common.exception.UnauthorizedException;
import com.carsharing.common.security.CustomUserDetailsService;
import com.carsharing.common.security.jwt.JwtService;
import com.carsharing.referral.service.ReferralService;
import com.carsharing.user.entity.Role;
import com.carsharing.user.entity.User;
import com.carsharing.user.entity.UserStatus;
import com.carsharing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ReferralService referralService;

    @Override
    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Паролі не співпадають");
        }

        String email = request.getEmail().toLowerCase().trim();
        String phone = request.getPhone().trim();

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Користувач з таким email вже існує");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new BadRequestException("Користувач з таким телефоном вже існує");
        }

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .phone(phone)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .referralCode(generateReferralCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        referralService.handleReferralOnRegistration(savedUser, request.getReferralCode());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Невірний email або пароль");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Користувача не знайдено"));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    private String generateReferralCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}