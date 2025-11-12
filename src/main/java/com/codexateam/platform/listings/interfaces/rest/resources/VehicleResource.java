package com.codexateam.platform.listings.interfaces.rest.resources;

import java.util.Date;

/**
 * DTO for returning vehicle data.
 */
public record VehicleResource(
        Long id,
        String brand,
        String model,
        Integer year,
        Double pricePerDay,
        String status,
        String imageUrl,
        Long ownerId,
        Date createdAt
) {
}
