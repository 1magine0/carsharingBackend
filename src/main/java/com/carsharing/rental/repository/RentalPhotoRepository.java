package com.carsharing.rental.repository;

import com.carsharing.rental.entity.Rental;
import com.carsharing.rental.entity.RentalPhoto;
import com.carsharing.rental.entity.RentalPhotoType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalPhotoRepository extends JpaRepository<RentalPhoto, Long> {

    List<RentalPhoto> findByRentalOrderByUploadedAtAsc(Rental rental);

    List<RentalPhoto> findByRentalAndPhotoTypeOrderByUploadedAtAsc(
            Rental rental,
            RentalPhotoType photoType
    );

    long countByRentalAndPhotoType(Rental rental, RentalPhotoType photoType);

    boolean existsByRentalAndPhotoType(Rental rental, RentalPhotoType photoType);
}