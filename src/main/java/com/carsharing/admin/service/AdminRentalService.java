package com.carsharing.admin.service;

import com.carsharing.admin.dto.AdminRentalResponse;
import com.carsharing.rental.dto.RentalPhotoResponse;
import com.carsharing.rental.entity.RentalPhotoType;

import java.util.List;

public interface AdminRentalService {

    List<AdminRentalResponse> getAllRentals();

    List<AdminRentalResponse> getActiveRentals();

    List<RentalPhotoResponse> getRentalPhotos(Long rentalId);

    List<RentalPhotoResponse> getRentalPhotosByType(Long rentalId, RentalPhotoType photoType);
}