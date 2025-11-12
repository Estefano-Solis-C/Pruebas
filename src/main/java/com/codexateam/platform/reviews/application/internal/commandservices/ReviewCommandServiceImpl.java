package com.codexateam.platform.reviews.application.internal.commandservices;

import com.codexateam.platform.reviews.domain.model.aggregates.Review;
import com.codexateam.platform.reviews.domain.model.commands.CreateReviewCommand;
import com.codexateam.platform.reviews.domain.services.ReviewCommandService;
import com.codexateam.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import com.codexateam.platform.booking.interfaces.acl.BookingContextFacade; // ACL
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of ReviewCommandService.
 */
@Service
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final BookingContextFacade bookingContextFacade; // ACL

    public ReviewCommandServiceImpl(ReviewRepository reviewRepository, BookingContextFacade bookingContextFacade) {
        this.reviewRepository = reviewRepository;
        this.bookingContextFacade = bookingContextFacade;
    }

    /**
     * Handles the CreateReviewCommand.
     * TODO: Add validation:
     * 1. Use IAM ACL to verify renterId exists.
     * 2. Use Booking ACL to verify this renterId has a *completed* booking for this vehicleId.
     * 3. Verify that the renter has not already submitted a review for this booking.
     */
    @Override
    public Optional<Review> handle(CreateReviewCommand command) {
        // 2. Validate completed booking exists
        boolean hasCompletedBooking = bookingContextFacade.hasCompletedBooking(command.renterId(), command.vehicleId());
        if (!hasCompletedBooking) {
            throw new IllegalArgumentException("Solo puedes dejar reseñas de reservas que ya has completado");
        }

        // 3. Prevent duplicate reviews per renter per vehicle
        if (reviewRepository.existsByVehicleIdAndRenterId(command.vehicleId(), command.renterId())) {
            throw new IllegalArgumentException("Ya has dejado una reseña para este vehículo");
        }

        var review = new Review(command);
        try {
            reviewRepository.save(review);
            return Optional.of(review);
        } catch (Exception e) {
            // Log error
            return Optional.empty();
        }
    }
}
