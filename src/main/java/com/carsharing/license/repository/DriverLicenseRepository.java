package com.carsharing.license.repository;

import com.carsharing.license.entity.DriverLicense;
import com.carsharing.license.entity.LicenseStatus;
import com.carsharing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverLicenseRepository extends JpaRepository<DriverLicense, Long> {

    Optional<DriverLicense> findByUser(User user);

    List<DriverLicense> findByVerificationStatus(LicenseStatus status);
}