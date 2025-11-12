package com.codexateam.platform.booking.application.internal.commandservices;

import com.codexateam.platform.booking.application.internal.outboundservices.acl.ExternalListingsService;
import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.commands.CreateBookingCommand;
import com.codexateam.platform.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import com.codexateam.platform.listings.interfaces.rest.resources.VehicleResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingCommandServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ExternalListingsService externalListingsService;

    @InjectMocks
    private BookingCommandServiceImpl service;

    private CreateBookingCommand baseCommand;
    private VehicleResource vehicle;

    @BeforeEach
    void setUp() {
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.DECEMBER, 1, 10, 0, 0);
        Date start = cal.getTime();
        cal.set(2025, Calendar.DECEMBER, 5, 10, 0, 0);
        Date end = cal.getTime();
        baseCommand = new CreateBookingCommand(1L, 100L, 200L, start, end);
        vehicle = new VehicleResource(1L, "Toyota", "Corolla", 2020, 100.0, "available", null, 200L, new Date());
    }

    @Test
    void shouldThrowWhenOverlap() {
        when(externalListingsService.fetchVehicleById(1L)).thenReturn(Optional.of(vehicle));
        when(bookingRepository.existsOverlappingBooking(eq(1L), any(Date.class), any(Date.class))).thenReturn(true);

        var ex = assertThrows(IllegalArgumentException.class, () -> service.handle(baseCommand));
        assertTrue(ex.getMessage().contains("El vehículo no está disponible para las fechas seleccionadas"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldSaveWhenNoOverlap() {
        when(externalListingsService.fetchVehicleById(1L)).thenReturn(Optional.of(vehicle));
        when(bookingRepository.existsOverlappingBooking(eq(1L), any(Date.class), any(Date.class))).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(baseCommand);
        assertTrue(result.isPresent());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }
}

