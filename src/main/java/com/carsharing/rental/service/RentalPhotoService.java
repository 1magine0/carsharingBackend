package com.carsharing.rental.service;

import com.carsharing.rental.dto.RentalPhotoResponse;
import com.carsharing.rental.entity.RentalPhotoType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RentalPhotoService {

    RentalPhotoResponse uploadPhoto(Long rentalId, RentalPhotoType photoType, MultipartFile image);

    List<RentalPhotoResponse> getRentalPhotos(Long rentalId);

    List<RentalPhotoResponse> getRentalPhotosByType(Long rentalId, RentalPhotoType photoType);

    void deletePhoto(Long photoId);
}