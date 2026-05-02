package com.carsharing.license.dto;

import com.carsharing.license.entity.LicenseStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseResponse {

    private Long id;
    private String documentNumber;
    private LicenseStatus status;
    private String rejectionReason;
    private String imageUrl;
}