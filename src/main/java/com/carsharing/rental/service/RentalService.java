package com.carsharing.rental.service;

import com.carsharing.rental.dto.CreateRentalRequest;
import com.carsharing.rental.dto.RentalPreviewRequest;
import com.carsharing.rental.dto.RentalPreviewResponse;
import com.carsharing.rental.dto.RentalResponse;

import java.util.List;

public interface RentalService {

    RentalPreviewResponse previewRental(RentalPreviewRequest request);

    void createRental(CreateRentalRequest request);

    List<RentalResponse> getCurrentUserRentals();

    RentalResponse getCurrentUserActiveRental();

    void finishRental(Long rentalId);
}