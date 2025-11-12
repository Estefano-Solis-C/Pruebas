package com.codexateam.platform.booking.domain.services;

import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.commands.CreateBookingCommand;
import java.util.Optional;

/**
 * Service interface for handling Booking commands.
 */
public interface BookingCommandService {
    /**
     * Handles the CreateBookingCommand.
     * @param command The command to create a booking.
     * @return An Optional containing the created Booking aggregate.
     */
    Optional<Booking> handle(CreateBookingCommand command);
    // TODO: Add handlers for ConfirmBookingCommand, CancelBookingCommand
}
