package com.codexateam.platform.reviews.domain.model.queries;

/**
 * Query to find all reviews for a specific vehicle.
 *
 */
public record GetReviewsByVehicleIdQuery(Long vehicleId) {
}
