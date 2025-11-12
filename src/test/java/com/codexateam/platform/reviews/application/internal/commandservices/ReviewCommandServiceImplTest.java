package com.codexateam.platform.reviews.application.internal.commandservices;

import com.codexateam.platform.booking.interfaces.acl.BookingContextFacade;
import com.codexateam.platform.reviews.domain.model.aggregates.Review;
import com.codexateam.platform.reviews.domain.model.commands.CreateReviewCommand;
import com.codexateam.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewCommandServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingContextFacade bookingContextFacade;

    @InjectMocks
    private ReviewCommandServiceImpl service;

    private CreateReviewCommand baseCommand;

    @BeforeEach
    void setUp() {
        baseCommand = new CreateReviewCommand(1L, 10L, 5, "Buen vehiculo");
    }

    @Test
    void shouldThrowWhenNoCompletedBooking() {
        when(bookingContextFacade.hasCompletedBooking(10L, 1L)).thenReturn(false);

        var ex = assertThrows(IllegalArgumentException.class, () -> service.handle(baseCommand));
        assertTrue(ex.getMessage().contains("Solo puedes dejar reseñas de reservas que ya has completado"));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDuplicateReview() {
        when(bookingContextFacade.hasCompletedBooking(10L, 1L)).thenReturn(true);
        when(reviewRepository.existsByVehicleIdAndRenterId(1L, 10L)).thenReturn(true);

        var ex = assertThrows(IllegalArgumentException.class, () -> service.handle(baseCommand));
        assertTrue(ex.getMessage().contains("Ya has dejado una reseña para este vehículo"));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldSaveWhenValid() {
        when(bookingContextFacade.hasCompletedBooking(10L, 1L)).thenReturn(true);
        when(reviewRepository.existsByVehicleIdAndRenterId(1L, 10L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Review> result = service.handle(baseCommand);
        assertTrue(result.isPresent());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}
