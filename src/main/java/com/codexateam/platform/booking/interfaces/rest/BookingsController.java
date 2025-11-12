package com.codexateam.platform.booking.interfaces.rest;

import com.codexateam.platform.booking.application.internal.outboundservices.acl.ExternalListingsService;
import com.codexateam.platform.booking.domain.model.queries.GetBookingsByOwnerIdQuery;
import com.codexateam.platform.booking.domain.model.queries.GetBookingsByRenterIdQuery;
import com.codexateam.platform.booking.domain.services.BookingCommandService;
import com.codexateam.platform.booking.domain.services.BookingQueryService;
import com.codexateam.platform.booking.interfaces.rest.resources.BookingResource;
import com.codexateam.platform.booking.interfaces.rest.resources.CreateBookingResource;
import com.codexateam.platform.booking.interfaces.rest.transform.BookingResourceFromEntityAssembler;
import com.codexateam.platform.booking.interfaces.rest.transform.CreateBookingCommandFromResourceAssembler;
import com.codexateam.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for the Booking bounded context.
 * Handles all API requests for creating and viewing bookings.
 *
 *
 */
@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Endpoints for managing bookings")
public class BookingsController {

    private final BookingCommandService bookingCommandService;
    private final BookingQueryService bookingQueryService;
    private final ExternalListingsService externalListingsService;

    public BookingsController(BookingCommandService bookingCommandService, BookingQueryService bookingQueryService, ExternalListingsService externalListingsService) {
        this.bookingCommandService = bookingCommandService;
        this.bookingQueryService = bookingQueryService;
        this.externalListingsService = externalListingsService;
    }

    /**
     * Extracts the authenticated user's ID from the security context.
     * @return The authenticated user's ID.
     */
    private Long getAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    /**
     * Creates a new booking.
     * Requires ARRENDATARIO role.
     * @param resource The booking data (vehicleId, dates).
     * @return The created booking resource.
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ARRENDATARIO')")
    public ResponseEntity<BookingResource> createBooking(@RequestBody CreateBookingResource resource) {
        Long renterId = getAuthenticatedUserId();
        
        // Use ACL to find the vehicle's owner ID
        var vehicle = externalListingsService.fetchVehicleById(resource.vehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        Long ownerId = vehicle.ownerId();

        var command = CreateBookingCommandFromResourceAssembler.toCommandFromResource(resource, renterId, ownerId);
        var booking = bookingCommandService.handle(command)
                .orElseThrow(() -> new RuntimeException("Error creating booking. Check dates or availability."));
        
        var bookingResource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResource);
    }

    /**
     * Gets all bookings made by the authenticated renter.
     * Corresponds to "My Bookings" page.
     *
     * @return A list of booking resources.
     */
    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('ROLE_ARRENDATARIO')")
    public ResponseEntity<List<BookingResource>> getMyBookingsAsRenter() {
        Long renterId = getAuthenticatedUserId();
        var query = new GetBookingsByRenterIdQuery(renterId);
        var bookings = bookingQueryService.handle(query);
        var resources = bookings.stream()
                .map(BookingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Gets all booking requests for vehicles owned by the authenticated owner.
     * Corresponds to "Booking Requests" page.
     *
     * @return A list of booking resources.
     */
    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('ROLE_ARRENDADOR')")
    public ResponseEntity<List<BookingResource>> getMyBookingRequestsAsOwner() {
        Long ownerId = getAuthenticatedUserId();
        var query = new GetBookingsByOwnerIdQuery(ownerId);
        var bookings = bookingQueryService.handle(query);
        var resources = bookings.stream()
                .map(BookingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
