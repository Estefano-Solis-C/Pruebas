package com.codexateam.platform.listings.domain.model.commands;

/**
 * Command to create a new vehicle listing.
 *
 * @param brand The vehicle's brand/manufacturer.
 * @param model The vehicle's model name.
 * @param year The vehicle's manufacturing year.
 * @param pricePerDay The daily rental price.
 * @param imageUrl URL of the vehicle's image.
 * @param ownerId The ID of the owner (must have ROLE_ARRENDADOR).
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
