package com.codexateam.platform.booking.domain.services;

import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.queries.GetBookingsByOwnerIdQuery;
import com.codexateam.platform.booking.domain.model.queries.GetBookingsByRenterIdQuery;

import java.util.List;

/**
 * Service interface for handling Booking queries.
 */
public interface BookingQueryService {
    /**
     * Handles the query to get bookings by renter ID.
     */
    List<Booking> handle(GetBookingsByRenterIdQuery query);
    
    /**
     * Handles the query to get bookings by owner ID.
     */
    List<Booking> handle(GetBookingsByOwnerIdQuery query);
}
