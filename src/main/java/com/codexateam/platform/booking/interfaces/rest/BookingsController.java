package com.codexateam.platform.booking.interfaces.rest;

import com.codexateam.platform.booking.application.internal.outboundservices.acl.ExternalListingsService;
import com.codexateam.platform.booking.domain.model.commands.CancelBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.ConfirmBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.RejectBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.UpdateBookingCommand;
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
import com.codexateam.platform.booking.domain.model.commands.DeleteBookingCommand;
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
 * REST Controller for the Booking bounded context.
 * Handles all API requests for creating and viewing bookings.
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
    @Operation(summary = "Create a new booking", description = "Create a new booking request for a vehicle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ARRENDATARIO')")
    public ResponseEntity<BookingResource> createBooking(@RequestBody CreateBookingResource resource) {
        Long renterId = getAuthenticatedUserId();
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
     * @return A list of booking resources.
     */
    @Operation(summary = "Get Renter's Bookings", description = "Get all bookings made by the authenticated renter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
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
     * @return A list of booking resources.
     */
    @Operation(summary = "Get Owner's Booking Requests", description = "Get all booking requests for vehicles owned by the authenticated owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requests found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
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
     * @param bookingId The ID of the booking to confirm.
     * @return The confirmed booking resource.
     */
    @Operation(summary = "Confirm Booking", description = "Confirm a pending booking request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking confirmed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PutMapping("/{bookingId}/confirm")
    @PreAuthorize("hasRole('ROLE_ARRENDADOR')")
    public ResponseEntity<BookingResource> confirmBooking(@PathVariable Long bookingId) {
        Long ownerId = getAuthenticatedUserId();
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
     * @param bookingId The ID of the booking to reject.
     * @return The rejected booking resource.
     */
    @Operation(summary = "Reject Booking", description = "Reject a pending booking request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking rejected"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PutMapping("/{bookingId}/reject")
    @PreAuthorize("hasRole('ROLE_ARRENDADOR')")
    public ResponseEntity<BookingResource> rejectBooking(@PathVariable Long bookingId) {
        Long ownerId = getAuthenticatedUserId();
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
     * @param bookingId The ID of the booking to cancel.
     * @return The canceled booking resource.
     */
    @Operation(summary = "Cancel Booking", description = "Cancel a booking as the renter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking canceled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
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
     * @param bookingId The ID of the booking to retrieve.
     * @return The booking resource.
     */
    @Operation(summary = "Get Booking by ID", description = "Get details for a specific booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/{bookingId}")
    @PreAuthorize("hasRole('ROLE_ARRENDADOR') or hasRole('ROLE_ARRENDATARIO')")
    public ResponseEntity<BookingResource> getBookingById(@PathVariable Long bookingId) {
        var booking = bookingQueryService.handle(new GetBookingByIdQuery(bookingId))
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        var resource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking);
        return ResponseEntity.ok(resource);
    }

    /**
     * Deletes a booking.
     * @param bookingId The ID of the booking to delete.
     * @return An empty response with status OK.
     */
    @Operation(summary = "Delete Booking", description = "Delete a booking by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking deleted"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long bookingId) {
        bookingCommandService.handle(new DeleteBookingCommand(bookingId));
        return ResponseEntity.ok().build();
    }

    /**
     * Updates an existing booking.
     * Allows the renter or owner to update the booking end date and recalculates the total price.
     * @param bookingId The ID of the booking to update.
     * @param resource The new booking data (end date).
     * @return The updated booking resource.
     */
    @Operation(summary = "Update/Renew Booking", description = "Update (renew) an existing booking's end date and total price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking updated"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PutMapping("/{bookingId}")
    @PreAuthorize("hasRole('ROLE_ARRENDATARIO') or hasRole('ROLE_ARRENDADOR')")
    public ResponseEntity<BookingResource> updateBooking(@PathVariable Long bookingId, @RequestBody CreateBookingResource resource) {
        // Fetch existing booking to validate it exists
        var existingBooking = bookingQueryService.handle(new GetBookingByIdQuery(bookingId))
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // For renewal/update we only allow changing the end date (start date remains original)
        // Ensure new endDate is after current startDate
        if (resource.endDate().before(existingBooking.getStartDate())) {
            throw new IllegalArgumentException("New end date must be after the original start date");
        }

        // Recalculate total price based on original startDate and new endDate
        long diffInMillis = Math.abs(resource.endDate().getTime() - existingBooking.getStartDate().getTime());
        long days = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        if (days == 0) days = 1; // Minimum 1 day

        // Get vehicle daily price via ACL
        var vehicle = externalListingsService.fetchVehicleById(existingBooking.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found for booking"));
        Double totalPrice = vehicle.pricePerDay() * days;

        var command = new UpdateBookingCommand(bookingId, resource.endDate(), totalPrice);
        var updatedBooking = bookingCommandService.handle(command)
                .orElseThrow(() -> new RuntimeException("Error updating booking"));

        var resourceOut = BookingResourceFromEntityAssembler.toResourceFromEntity(updatedBooking);
        return ResponseEntity.ok(resourceOut);
    }
}
