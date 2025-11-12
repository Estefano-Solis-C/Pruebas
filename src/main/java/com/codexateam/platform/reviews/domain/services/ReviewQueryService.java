package com.codexateam.platform.reviews.domain.services;

import com.codexateam.platform.reviews.domain.model.aggregates.Review;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewsByRenterIdQuery;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewsByVehicleIdQuery;

import java.util.List;

/**
 * Service interface for handling Review queries.
 */
public interface ReviewQueryService {
    /**
     * Handles the query to get reviews by vehicle ID.
     */
    List<Review> handle(GetReviewsByVehicleIdQuery query);
    
    /**
     * Handles the query to get reviews by renter ID.
     */
    List<Review> handle(GetReviewsByRenterIdQuery query);
}
