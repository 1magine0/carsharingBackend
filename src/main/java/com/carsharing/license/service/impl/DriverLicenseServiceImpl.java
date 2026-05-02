package com.carsharing.license.service.impl;

import com.carsharing.common.exception.BadRequestException;
import com.carsharing.common.exception.NotFoundException;
import com.carsharing.common.storage.FileStorageService;
import com.carsharing.common.storage.UploadedFileResponse;
import com.carsharing.license.dto.LicenseResponse;
import com.carsharing.license.entity.DriverLicense;
import com.carsharing.license.entity.LicenseStatus;
import com.carsharing.license.repository.DriverLicenseRepository;
import com.carsharing.license.service.DriverLicenseService;
import com.carsharing.user.entity.User;
import com.carsharing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverLicenseServiceImpl implements DriverLicenseService {

    private final DriverLicenseRepository repository;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Override
    public void upload(String documentNumber, LocalDate issueDate, LocalDate expiryDate, MultipartFile image) {
        User user = userService.getCurrentUserEntity();

        if (documentNumber == null || documentNumber.isBlank()) {
            throw new BadRequestException("Номер документа є обов'язковим");
        }

        if (issueDate == null || expiryDate == null) {
            throw new BadRequestException("Дати посвідчення є обов'язковими");
        }

        if (!expiryDate.isAfter(issueDate)) {
            throw new BadRequestException("Дата завершення дії повинна бути пізнішою за дату видачі");
        }

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Зображення посвідчення є обов'язковим");
        }

        if (repository.findByUser(user).isPresent()) {
            throw new BadRequestException("Посвідчення вже завантажено");
        }

        UploadedFileResponse uploadedFile = fileStorageService.upload(image, "licenses");

        DriverLicense license = DriverLicense.builder()
                .user(user)
                .documentNumber(documentNumber)
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .imageUrl(uploadedFile.getUrl())
                .imagePublicId(uploadedFile.getPublicId())
                .verificationStatus(LicenseStatus.PENDING)
                .build();

        repository.save(license);
    }

    @Override
    public LicenseResponse getMyLicense() {
        User user = userService.getCurrentUserEntity();

        DriverLicense license = repository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Посвідчення не знайдено"));

        return map(license);
    }

    @Override
    public List<LicenseResponse> getPending() {
        return repository.findByVerificationStatus(LicenseStatus.PENDING)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void approve(Long id) {
        DriverLicense license = getById(id);

        license.setVerificationStatus(LicenseStatus.APPROVED);
        license.setVerifiedAt(LocalDateTime.now());

        repository.save(license);
    }

    @Override
    public void reject(Long id, String reason) {
        DriverLicense license = getById(id);

        license.setVerificationStatus(LicenseStatus.REJECTED);
        license.setRejectionReason(reason);
        license.setVerifiedAt(LocalDateTime.now());

        repository.save(license);
    }

    private DriverLicense getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Посвідчення не знайдено"));
    }

    private LicenseResponse map(DriverLicense license) {
        return LicenseResponse.builder()
                .id(license.getId())
                .documentNumber(license.getDocumentNumber())
                .status(license.getVerificationStatus())
                .rejectionReason(license.getRejectionReason())
                .imageUrl(license.getImageUrl())
                .build();
    }
}