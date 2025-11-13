package com.codexateam.platform.booking.interfaces.rest;

import com.codexateam.platform.booking.application.internal.outboundservices.acl.ExternalListingsService;
import com.codexateam.platform.booking.domain.model.commands.CancelBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.ConfirmBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.RejectBookingCommand;
import com.codexateam.platform.booking.domain.model.queries.GetBookingsByOwnerIdQuery;
import com.codexateam.platform.booking.domain.model.queries.GetBookingsByRenterIdQuery;
import com.codexateam.platform.booking.domain.model.queries.GetBookingByIdQuery;
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
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("User not authenticated");
        }
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

    /**
     * Confirms a booking request.
     * Only the vehicle owner (ARRENDADOR) can confirm bookings for their vehicles.
     *
     * @param bookingId The ID of the booking to confirm.
     * @return The confirmed booking resource.
     */
    @PutMapping("/{bookingId}/confirm")
    @PreAuthorize("hasRole('ROLE_ARRENDADOR')")
    public ResponseEntity<BookingResource> confirmBooking(@PathVariable Long bookingId) {
        Long ownerId = getAuthenticatedUserId();

        // First, get the booking to verify ownership
        var bookingOpt = bookingQueryService.handle(new GetBookingsByOwnerIdQuery(ownerId))
                .stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst();

        if (bookingOpt.isEmpty()) {
            throw new SecurityException("You are not authorized to confirm this booking.");
        }

        var command = new ConfirmBookingCommand(bookingId);
        var booking = bookingCommandService.handle(command)
                .orElseThrow(() -> new RuntimeException("Error confirming booking."));

        var resource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking);
        return ResponseEntity.ok(resource);
    }

    /**
     * Rejects a booking request.
     * Only the vehicle owner (ARRENDADOR) can reject bookings for their vehicles.
     *
     * @param bookingId The ID of the booking to reject.
     * @return The rejected booking resource.
     */
    @PutMapping("/{bookingId}/reject")
    @PreAuthorize("hasRole('ROLE_ARRENDADOR')")
    public ResponseEntity<BookingResource> rejectBooking(@PathVariable Long bookingId) {
        Long ownerId = getAuthenticatedUserId();

        // First, get the booking to verify ownership
        var bookingOpt = bookingQueryService.handle(new GetBookingsByOwnerIdQuery(ownerId))
                .stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst();

        if (bookingOpt.isEmpty()) {
            throw new SecurityException("You are not authorized to reject this booking.");
        }

        var command = new RejectBookingCommand(bookingId);
        var booking = bookingCommandService.handle(command)
                .orElseThrow(() -> new RuntimeException("Error rejecting booking."));

        var resource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking);
        return ResponseEntity.ok(resource);
    }

    /**
     * Cancels a booking.
     * Only the renter (ARRENDATARIO) who created the booking can cancel it.
     *
     * @param bookingId The ID of the booking to cancel.
     * @return The canceled booking resource.
     */
    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('ROLE_ARRENDATARIO')")
    public ResponseEntity<BookingResource> cancelBooking(@PathVariable Long bookingId) {
        Long renterId = getAuthenticatedUserId();

        var command = new CancelBookingCommand(bookingId, renterId);
        var booking = bookingCommandService.handle(command)
                .orElseThrow(() -> new RuntimeException("Error canceling booking."));

        var resource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking);
        return ResponseEntity.ok(resource);
    }

    /**
     * Gets a booking by its ID.
     *
     * @param bookingId The ID of the booking to retrieve.
     * @return The booking resource.
     */
    @GetMapping("/{bookingId}")
    @PreAuthorize("hasRole('ROLE_ARRENDADOR') or hasRole('ROLE_ARRENDATARIO')")
    public ResponseEntity<BookingResource> getBookingById(@PathVariable Long bookingId) {
        var booking = bookingQueryService.handle(new GetBookingByIdQuery(bookingId))
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        var resource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking);
        return ResponseEntity.ok(resource);
    }
}
