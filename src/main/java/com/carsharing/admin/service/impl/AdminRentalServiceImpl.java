package com.carsharing.admin.service.impl;

import com.carsharing.admin.dto.AdminRentalResponse;
import com.carsharing.admin.service.AdminRentalService;
import com.carsharing.rental.entity.Rental;
import com.carsharing.rental.entity.RentalStatus;
import com.carsharing.rental.repository.RentalRepository;
import com.carsharing.common.exception.NotFoundException;
import com.carsharing.rental.dto.RentalPhotoResponse;
import com.carsharing.rental.entity.RentalPhoto;
import com.carsharing.rental.entity.RentalPhotoType;
import com.carsharing.rental.repository.RentalPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRentalServiceImpl implements AdminRentalService {

    private final RentalRepository rentalRepository;
    private final RentalPhotoRepository rentalPhotoRepository;

    @Override
    public List<AdminRentalResponse> getAllRentals() {
        return rentalRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AdminRentalResponse> getActiveRentals() {
        return rentalRepository.findByStatus(RentalStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AdminRentalResponse mapToResponse(Rental rental) {
        return AdminRentalResponse.builder()
                .id(rental.getId())

                .userId(rental.getUser().getId())
                .userFullName(rental.getUser().getFullName())
                .userEmail(rental.getUser().getEmail())
                .userPhone(rental.getUser().getPhone())

                .carId(rental.getCar().getId())
                .carBrand(rental.getCar().getBrand())
                .carModel(rental.getCar().getModel())
                .carRegistrationNumber(rental.getCar().getRegistrationNumber())

                .tariffType(rental.getTariffType())
                .startTime(rental.getStartTime())
                .endTime(rental.getEndTime())

                .totalPrice(rental.getTotalPrice())
                .bonusUsed(rental.getBonusUsed())
                .discountAmount(rental.getDiscountAmount())

                .beforePhotoCount(
                        rentalPhotoRepository.countByRentalAndPhotoType(rental, RentalPhotoType.BEFORE)
                )
                .afterPhotoCount(
                        rentalPhotoRepository.countByRentalAndPhotoType(rental, RentalPhotoType.AFTER)
                )

                .status(rental.getStatus())
                .build();
    }

    @Override
    public List<RentalPhotoResponse> getRentalPhotos(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("Оренду не знайдено"));

        return rentalPhotoRepository.findByRentalOrderByUploadedAtAsc(rental)
                .stream()
                .map(this::mapPhotoToResponse)
                .toList();
    }

    @Override
    public List<RentalPhotoResponse> getRentalPhotosByType(Long rentalId, RentalPhotoType photoType) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("Оренду не знайдено"));

        return rentalPhotoRepository.findByRentalAndPhotoTypeOrderByUploadedAtAsc(rental, photoType)
                .stream()
                .map(this::mapPhotoToResponse)
                .toList();
    }

    private RentalPhotoResponse mapPhotoToResponse(RentalPhoto photo) {
        return RentalPhotoResponse.builder()
                .id(photo.getId())
                .rentalId(photo.getRental().getId())
                .photoType(photo.getPhotoType())
                .imageUrl(photo.getImageUrl())
                .uploadedAt(photo.getUploadedAt())
                .build();
    }
}