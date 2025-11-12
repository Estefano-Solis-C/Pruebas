package com.codexateam.platform.reviews.domain.model.queries;

/**
 * Query to find all reviews written by a specific renter (Arrendatario).
 */
public record GetReviewsByRenterIdQuery(Long renterId) {
}
