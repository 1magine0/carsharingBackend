package com.carsharing.license.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadLicenseRequest {

    @NotBlank
    private String documentNumber;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    private String imageUrl;
}