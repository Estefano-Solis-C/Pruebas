package com.codexateam.platform.listings.domain.model.commands;

/**
 * Command to create a new vehicle listing.
 */
public record CreateVehicleCommand(
        String brand,
        String model,
        Integer year,
        Double pricePerDay,
        String imageUrl,
        Long ownerId
) {
}
