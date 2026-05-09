package com.carsharing.rental.service;

import com.carsharing.rental.dto.*;

import java.util.List;

public interface RentalService {

    RentalPreviewResponse previewRental(RentalPreviewRequest request);

    void createRental(CreateRentalRequest request);

    List<RentalResponse> getCurrentUserRentals();

    RentalResponse getCurrentUserActiveRental();

    void finishRental(Long rentalId);

    UnlockCarResponse unlockCar(Long rentalId);
}