package com.codexateam.platform.listings.interfaces.rest.resources;

/**
 * DTO for creating a new vehicle.
 * 'ownerId' will be taken from the authenticated token, not this resource.
 */
public record CreateVehicleResource(
        String brand,
        String model,
        Integer year,
        Double pricePerDay,
        String imageUrl
) {
}
