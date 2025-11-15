package com.codexateam.platform.reviews.interfaces.rest;

import com.codexateam.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewsByRenterIdQuery;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewsByVehicleIdQuery;
import com.codexateam.platform.reviews.domain.services.ReviewCommandService;
import com.codexateam.platform.reviews.domain.services.ReviewQueryService;
import com.codexateam.platform.reviews.interfaces.rest.resources.CreateReviewResource;
import com.codexateam.platform.reviews.interfaces.rest.resources.ReviewResource;
import com.codexateam.platform.reviews.interfaces.rest.transform.CreateReviewCommandFromResourceAssembler;
import com.codexateam.platform.reviews.interfaces.rest.transform.ReviewResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing reviews.
 * It provides endpoints for creating and retrieving reviews.
 */
@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "Endpoints for managing reviews")
public class ReviewsController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    public ReviewsController(ReviewCommandService reviewCommandService, ReviewQueryService reviewQueryService) {
        this.reviewCommandService = reviewCommandService;
        this.reviewQueryService = reviewQueryService;
    }

    /**
     * Retrieves the ID of the currently authenticated user.
     * @return The user ID.
     * @throws SecurityException if the user is not authenticated.
     */
    private Long getAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("User not authenticated");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    /**
     * Creates a new review.
     * This endpoint is restricted to users with the 'ROLE_ARRENDATARIO' role.
     * @param resource The resource containing the data for the new review.
     * @return A ResponseEntity containing the created review resource and a status of CREATED.
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ARRENDATARIO')")
    @Operation(summary = "Create Review", description = "Create a new review for a vehicle (Renter only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created"),
            @ApiResponse(responseCode = "400", description = "Bad Request (e.g., no completed booking)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ReviewResource> createReview(@RequestBody CreateReviewResource resource) {
        Long renterId = getAuthenticatedUserId();
        var command = CreateReviewCommandFromResourceAssembler.toCommandFromResource(resource, renterId);
        
        var review = reviewCommandService.handle(command)
                .orElseThrow(() -> new RuntimeException("Error creating review."));
        
        var reviewResource = ReviewResourceFromEntityAssembler.toResourceFromEntity(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResource);
    }

    /**
     * Retrieves all reviews for a specific vehicle.
     * @param vehicleId The ID of the vehicle.
     * @return A ResponseEntity containing a list of review resources and a status of OK.
     */
    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get Reviews by Vehicle ID", description = "Get all reviews for a specific vehicle (Public)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews found")
    })
    public ResponseEntity<List<ReviewResource>> getReviewsByVehicleId(@PathVariable Long vehicleId) {
        var query = new GetReviewsByVehicleIdQuery(vehicleId);
        var reviews = reviewQueryService.handle(query);
        var resources = reviews.stream()
                .map(ReviewResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
    

    /**
     * Retrieves all reviews written by the currently authenticated user.
     * This endpoint is restricted to users with the 'ROLE_ARRENDATARIO' role.
     * @return A ResponseEntity containing a list of review resources and a status of OK.
     */
    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('ROLE_ARRENDATARIO')")
    @Operation(summary = "Get Renter's Reviews", description = "Get all reviews written by the authenticated renter (ARRENDATARIO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<ReviewResource>> getMyReviews() {
        Long renterId = getAuthenticatedUserId();
        var query = new GetReviewsByRenterIdQuery(renterId);
        var reviews = reviewQueryService.handle(query);
        var resources = reviews.stream()
                .map(ReviewResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
