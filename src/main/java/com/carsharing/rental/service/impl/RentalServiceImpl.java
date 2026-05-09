package com.carsharing.rental.service.impl;

import com.carsharing.bonus.entity.BonusOperationType;
import com.carsharing.bonus.entity.BonusTransaction;
import com.carsharing.bonus.repository.BonusTransactionRepository;
import com.carsharing.car.entity.Car;
import com.carsharing.car.entity.CarStatus;
import com.carsharing.car.repository.CarRepository;
import com.carsharing.common.exception.BadRequestException;
import com.carsharing.common.exception.NotFoundException;
import com.carsharing.license.entity.DriverLicense;
import com.carsharing.license.entity.LicenseStatus;
import com.carsharing.license.repository.DriverLicenseRepository;
import com.carsharing.payment.entity.PaymentStatus;
import com.carsharing.payment.repository.PaymentRepository;
import com.carsharing.rental.dto.*;
import com.carsharing.rental.entity.Rental;
import com.carsharing.rental.entity.RentalStatus;
import com.carsharing.rental.repository.RentalRepository;
import com.carsharing.rental.service.RentalService;
import com.carsharing.user.entity.User;
import com.carsharing.user.service.UserService;
import com.carsharing.rental.entity.RentalPhotoType;
import com.carsharing.rental.repository.RentalPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserService userService;
    private final DriverLicenseRepository driverLicenseRepository;
    private final BonusTransactionRepository bonusTransactionRepository;
    private final RentalPhotoRepository rentalPhotoRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public RentalPreviewResponse previewRental(RentalPreviewRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        Car car = getAvailableCar(request.getCarId());

        validateRentalPeriod(request.getStartTime(), request.getEndTime());

        BigDecimal basePrice = calculatePrice(car, request.getTariffType(), request.getStartTime(), request.getEndTime());
        BigDecimal availableBonusBalance = getUserBonusBalance(currentUser);
        BigDecimal maxAllowedByPrice = calculateMaxBonusUsage(basePrice);
        BigDecimal maxBonusUsage = availableBonusBalance.min(maxAllowedByPrice);
        BigDecimal finalPrice = basePrice.subtract(maxBonusUsage).max(BigDecimal.ZERO);

        return RentalPreviewResponse.builder()
                .basePrice(basePrice)
                .availableBonusBalance(availableBonusBalance)
                .maxBonusUsage(maxBonusUsage)
                .finalPrice(finalPrice)
                .build();
    }

    @Override
    public void createRental(CreateRentalRequest request) {
        validateRentalPeriod(request.getStartTime(), request.getEndTime());

        User currentUser = userService.getCurrentUserEntity();

        boolean hasActiveRental = rentalRepository.existsByUserAndStatus(currentUser, RentalStatus.ACTIVE);
        if (hasActiveRental) {
            throw new BadRequestException("У вас вже є активна оренда. Спочатку завершіть поточну.");
        }

        validateApprovedLicense(currentUser);

        Car car = getAvailableCar(request.getCarId());

        BigDecimal basePrice = calculatePrice(car, request.getTariffType(), request.getStartTime(), request.getEndTime());
        BigDecimal availableBonusBalance = getUserBonusBalance(currentUser);
        BigDecimal maxAllowedByPrice = calculateMaxBonusUsage(basePrice);
        BigDecimal maxBonusUsage = availableBonusBalance.min(maxAllowedByPrice);

        if (request.getBonusUsed() == null) {
            throw new BadRequestException("Потрібно вказати кількість бонусів");
        }

        if (request.getBonusUsed().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Бонуси не можуть бути від'ємними");
        }

        if (request.getBonusUsed().compareTo(maxBonusUsage) > 0) {
            throw new BadRequestException("Кількість бонусів перевищує допустимий ліміт");
        }

        BigDecimal finalPrice = basePrice.subtract(request.getBonusUsed()).max(BigDecimal.ZERO);

        Rental rental = Rental.builder()
                .user(currentUser)
                .car(car)
                .tariffType(request.getTariffType())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .totalPrice(finalPrice)
                .bonusUsed(request.getBonusUsed())
                .discountAmount(BigDecimal.ZERO)
                .status(RentalStatus.BOOKED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        rentalRepository.save(rental);

        car.setStatus(CarStatus.RESERVED);
        car.setUpdatedAt(LocalDateTime.now());
        carRepository.save(car);

        if (request.getBonusUsed().compareTo(BigDecimal.ZERO) > 0) {
            bonusTransactionRepository.save(
                    BonusTransaction.builder()
                            .user(currentUser)
                            .rental(rental)
                            .amount(request.getBonusUsed())
                            .operationType(BonusOperationType.SPEND)
                            .description("Списання бонусів при створенні оренди")
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }
    }

    @Override
    public List<RentalResponse> getCurrentUserRentals() {
        User currentUser = userService.getCurrentUserEntity();

        return rentalRepository.findByUser(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public RentalResponse getCurrentUserActiveRental() {
        User currentUser = userService.getCurrentUserEntity();

        return rentalRepository.findByUser(currentUser)
                .stream()
                .filter(rental -> rental.getStatus() == RentalStatus.ACTIVE)
                .findFirst()
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Активну оренду не знайдено"));
    }

    @Override
    public void finishRental(Long rentalId) {
        User currentUser = userService.getCurrentUserEntity();

        Rental rental = rentalRepository.findByIdAndUser(rentalId, currentUser)
                .orElseThrow(() -> new NotFoundException("Оренду не знайдено"));

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BadRequestException("Завершити можна тільки активну оренду");
        }

        boolean hasBeforePhoto = rentalPhotoRepository.existsByRentalAndPhotoType(
                rental,
                RentalPhotoType.BEFORE
        );

        if (!hasBeforePhoto) {
            throw new BadRequestException("Перед початком користування авто потрібно завантажити фото до оренди");
        }

        boolean hasAfterPhoto = rentalPhotoRepository.existsByRentalAndPhotoType(
                rental,
                RentalPhotoType.AFTER
        );

        if (!hasAfterPhoto) {
            throw new BadRequestException("Перед завершенням оренди потрібно завантажити фото після");
        }

        rental.setStatus(RentalStatus.FINISHED);
        rental.setUpdatedAt(LocalDateTime.now());
        rentalRepository.save(rental);

        Car car = rental.getCar();
        car.setStatus(CarStatus.AVAILABLE);
        car.setUpdatedAt(LocalDateTime.now());
        carRepository.save(car);
    }

    @Override
    public UnlockCarResponse unlockCar(Long rentalId) {
        User currentUser = userService.getCurrentUserEntity();

        Rental rental = rentalRepository.findByIdAndUser(rentalId, currentUser)
                .orElseThrow(() -> new NotFoundException("Оренду не знайдено"));

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BadRequestException("Розблокувати авто можна тільки для активної оренди");
        }

        validateApprovedLicense(currentUser);

        boolean hasPaidPayment = paymentRepository
                .findFirstByRentalAndStatusOrderByCreatedAtDesc(rental, PaymentStatus.PAID)
                .isPresent();

        if (!hasPaidPayment) {
            throw new BadRequestException("Оренда ще не оплачена");
        }

        boolean hasBeforePhoto = rentalPhotoRepository.existsByRentalAndPhotoType(
                rental,
                RentalPhotoType.BEFORE
        );

        if (!hasBeforePhoto) {
            throw new BadRequestException("Перед розблокуванням авто потрібно завантажити фото до оренди");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(rental.getStartTime())) {
            throw new BadRequestException("Оренда ще не почалася");
        }

        if (now.isAfter(rental.getEndTime())) {
            throw new BadRequestException("Час оренди вже завершився");
        }

        return UnlockCarResponse.builder()
                .unlockAllowed(true)
                .rentalId(rental.getId())
                .carId(rental.getCar().getId())
                .carName(rental.getCar().getBrand() + " " + rental.getCar().getModel())
                .message("Авто розблоковано. NFC-перевірку успішно пройдено.")
                .build();
    }

    private void validateRentalPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new BadRequestException("Час завершення оренди повинен бути пізнішим за час початку");
        }
    }

    private void validateApprovedLicense(User user) {
        DriverLicense license = driverLicenseRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Посвідчення не знайдено"));

        if (license.getVerificationStatus() != LicenseStatus.APPROVED) {
            throw new BadRequestException("Посвідчення не підтверджене");
        }
    }

    private Car getAvailableCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Автомобіль не знайдено"));

        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new BadRequestException("Автомобіль недоступний для оренди");
        }

        return car;
    }

    private BigDecimal getUserBonusBalance(User user) {
        List<BonusTransaction> transactions = bonusTransactionRepository.findByUserOrderByCreatedAtDesc(user);

        return transactions.stream()
                .map(this::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .max(BigDecimal.ZERO);
    }

    private BigDecimal signedAmount(BonusTransaction transaction) {
        return switch (transaction.getOperationType()) {
            case EARN, REFERRAL -> transaction.getAmount();
            case SPEND -> transaction.getAmount().negate();
        };
    }

    private BigDecimal calculateMaxBonusUsage(BigDecimal basePrice) {
        return basePrice
                .multiply(new BigDecimal("0.10"))
                .setScale(2, RoundingMode.DOWN);
    }

    private BigDecimal calculatePrice(Car car, com.carsharing.rental.entity.TariffType tariffType,
                                      LocalDateTime startTime, LocalDateTime endTime) {
        return switch (tariffType) {
            case HOUR -> {
                Duration duration = Duration.between(startTime, endTime);
                long hours = duration.toHours();
                if (duration.toMinutes() % 60 != 0) {
                    hours++;
                }
                if (hours < 1) {
                    hours = 1;
                }
                yield car.getPricePerHour().multiply(BigDecimal.valueOf(hours));
            }
            case DAY -> {
                Duration duration = Duration.between(startTime, endTime);
                long days = duration.toDays();
                if (duration.toHours() % 24 != 0) {
                    days++;
                }
                if (days < 1) {
                    days = 1;
                }
                yield car.getPricePerDay().multiply(BigDecimal.valueOf(days));
            }
            case MONTH -> {
                int startYear = startTime.getYear();
                int startMonth = startTime.getMonthValue();
                int endYear = endTime.getYear();
                int endMonth = endTime.getMonthValue();

                long months = (endYear - startYear) * 12L + (endMonth - startMonth);
                if (months < 1) {
                    months = 1;
                }

                yield car.getPricePerMonth().multiply(BigDecimal.valueOf(months));
            }
        };
    }

    private RentalResponse mapToResponse(Rental rental) {
        return RentalResponse.builder()
                .id(rental.getId())
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
                .status(rental.getStatus())
                .build();
    }
}