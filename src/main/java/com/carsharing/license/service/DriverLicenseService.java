package com.carsharing.license.service;

import com.carsharing.license.dto.LicenseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface DriverLicenseService {

    void upload(String documentNumber, LocalDate issueDate, LocalDate expiryDate, MultipartFile image);

    LicenseResponse getMyLicense();

    List<LicenseResponse> getPending();

    void approve(Long id);

    void reject(Long id, String reason);
}