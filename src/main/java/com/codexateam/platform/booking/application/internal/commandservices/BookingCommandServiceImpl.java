package com.codexateam.platform.booking.application.internal.commandservices;

import com.codexateam.platform.booking.application.internal.outboundservices.acl.ExternalListingsService;
import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.commands.CreateBookingCommand;
import com.codexateam.platform.booking.domain.services.BookingCommandService;
import com.codexateam.platform.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of BookingCommandService.
 */
@Service
public class BookingCommandServiceImpl implements BookingCommandService {

    private final BookingRepository bookingRepository;
    private final ExternalListingsService externalListingsService;

    public BookingCommandServiceImpl(BookingRepository bookingRepository, ExternalListingsService externalListingsService) {
        this.bookingRepository = bookingRepository;
        this.externalListingsService = externalListingsService;
    }

    /**
     * Handles the CreateBookingCommand.
     * 1. Fetches vehicle data (price, owner) via ACL.
     * 2. Validates dates.
     * 3. Calculates total price.
     * 4. Saves the new booking.
     * 5. TODO: Update vehicle status via ACL.
     */
    @Override
    public Optional<Booking> handle(CreateBookingCommand command) {
        
        // 1. Fetch external data using ACL
        var vehicleResource = externalListingsService.fetchVehicleById(command.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle with ID " + command.vehicleId() + " not found."));

        // 2. Validate data
        if (!vehicleResource.ownerId().equals(command.ownerId())) {
            throw new IllegalArgumentException("Owner ID mismatch for the given vehicle.");
        }
        if (command.startDate().after(command.endDate())) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
        
        // TODO: Add validation for overlapping bookings
        // if (bookingRepository.existsOverlappingBooking(command.vehicleId(), command.startDate(), command.endDate())) {
        //    throw new IllegalArgumentException("Vehicle is not available for these dates");
        // }

        // 3. Calculate total price (from db.json)
        long diffInMillis = Math.abs(command.endDate().getTime() - command.startDate().getTime());
        long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        Double totalPrice = vehicleResource.pricePerDay() * (days == 0 ? 1 : days); // Minimum 1 day rental

        // 4. Create and save aggregate
        var booking = new Booking(command, totalPrice);
        try {
            bookingRepository.save(booking);
            
            // 5. TODO: Notify Listings context to update vehicle status
            // externalListingsService.updateVehicleStatus(command.vehicleId(), "rented");
            
            return Optional.of(booking);
        } catch (Exception e) {
            // Log error
            return Optional.empty();
        }
    }
}
