package com.codexateam.platform.reviews.application.internal.commandservices;

import com.codexateam.platform.reviews.domain.model.aggregates.Review;
import com.codexateam.platform.reviews.domain.model.commands.CreateReviewCommand;
import com.codexateam.platform.reviews.domain.services.ReviewCommandService;
import com.codexateam.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
// TODO: Inject ACL facades for IAM and Booking
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of ReviewCommandService.
 */
@Service
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private final ReviewRepository reviewRepository;
    // private final ExternalBookingService externalBookingService; // ACL

    public ReviewCommandServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
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
        // boolean hasCompletedBooking = externalBookingService
        //        .hasCompletedBooking(command.renterId(), command.vehicleId());
        // if (!hasCompletedBooking) {
        //    throw new IllegalArgumentException("User must have a completed booking to review this vehicle.");
        // }

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
