package com.carsharing.rental.service.impl;

import com.carsharing.common.exception.BadRequestException;
import com.carsharing.common.exception.NotFoundException;
import com.carsharing.common.storage.FileStorageService;
import com.carsharing.common.storage.UploadedFileResponse;
import com.carsharing.rental.dto.RentalPhotoResponse;
import com.carsharing.rental.entity.Rental;
import com.carsharing.rental.entity.RentalPhoto;
import com.carsharing.rental.entity.RentalPhotoType;
import com.carsharing.rental.entity.RentalStatus;
import com.carsharing.rental.repository.RentalPhotoRepository;
import com.carsharing.rental.repository.RentalRepository;
import com.carsharing.rental.service.RentalPhotoService;
import com.carsharing.user.entity.User;
import com.carsharing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalPhotoServiceImpl implements RentalPhotoService {

    private static final int MAX_PHOTOS_PER_TYPE = 6;

    private final RentalPhotoRepository rentalPhotoRepository;
    private final RentalRepository rentalRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Override
    public RentalPhotoResponse uploadPhoto(Long rentalId, RentalPhotoType photoType, MultipartFile image) {
        User currentUser = userService.getCurrentUserEntity();
        Rental rental = getUserRental(rentalId, currentUser);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BadRequestException("Фото можна додавати тільки до активної оренди");
        }

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Фото є обов'язковим");
        }

        long currentCount = rentalPhotoRepository.countByRentalAndPhotoType(rental, photoType);

        if (currentCount >= MAX_PHOTOS_PER_TYPE) {
            throw new BadRequestException("Максимальна кількість фото для цього типу — 6");
        }

        String folder = photoType == RentalPhotoType.BEFORE
                ? "rentals/before"
                : "rentals/after";

        UploadedFileResponse uploaded = fileStorageService.upload(image, folder);

        RentalPhoto photo = RentalPhoto.builder()
                .rental(rental)
                .photoType(photoType)
                .imageUrl(uploaded.getUrl())
                .imagePublicId(uploaded.getPublicId())
                .uploadedAt(LocalDateTime.now())
                .build();

        RentalPhoto savedPhoto = rentalPhotoRepository.save(photo);

        return map(savedPhoto);
    }

    @Override
    public List<RentalPhotoResponse> getRentalPhotos(Long rentalId) {
        User currentUser = userService.getCurrentUserEntity();
        Rental rental = getUserRental(rentalId, currentUser);

        return rentalPhotoRepository.findByRentalOrderByUploadedAtAsc(rental)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<RentalPhotoResponse> getRentalPhotosByType(Long rentalId, RentalPhotoType photoType) {
        User currentUser = userService.getCurrentUserEntity();
        Rental rental = getUserRental(rentalId, currentUser);

        return rentalPhotoRepository.findByRentalAndPhotoTypeOrderByUploadedAtAsc(rental, photoType)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void deletePhoto(Long photoId) {
        User currentUser = userService.getCurrentUserEntity();

        RentalPhoto photo = rentalPhotoRepository.findById(photoId)
                .orElseThrow(() -> new NotFoundException("Фото не знайдено"));

        if (!photo.getRental().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Ви не можете видалити це фото");
        }

        if (photo.getRental().getStatus() != RentalStatus.ACTIVE) {
            throw new BadRequestException("Фото можна видаляти тільки в активній оренді");
        }

        fileStorageService.delete(photo.getImagePublicId());
        rentalPhotoRepository.delete(photo);
    }

    private Rental getUserRental(Long rentalId, User user) {
        return rentalRepository.findByIdAndUser(rentalId, user)
                .orElseThrow(() -> new NotFoundException("Оренду не знайдено"));
    }

    private RentalPhotoResponse map(RentalPhoto photo) {
        return RentalPhotoResponse.builder()
                .id(photo.getId())
                .rentalId(photo.getRental().getId())
                .photoType(photo.getPhotoType())
                .imageUrl(photo.getImageUrl())
                .uploadedAt(photo.getUploadedAt())
                .build();
    }
}