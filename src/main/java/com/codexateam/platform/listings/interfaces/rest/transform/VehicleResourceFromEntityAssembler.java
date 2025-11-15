package com.codexateam.platform.listings.interfaces.rest.transform;

import com.codexateam.platform.listings.domain.model.aggregates.Vehicle;
import com.codexateam.platform.listings.interfaces.rest.resources.VehicleResource;

/**
 * Assembler to convert Vehicle aggregate to VehicleResource DTO.
 */
public class VehicleResourceFromEntityAssembler {
    /**
     * Converts a Vehicle domain entity to a VehicleResource DTO.
     *
     * @param entity The Vehicle aggregate.
     * @return The VehicleResource DTO.
     */
    public static VehicleResource toResourceFromEntity(Vehicle entity) {
        return new VehicleResource(
                entity.getId(),
                entity.getBrand(),
                entity.getModel(),
                entity.getYear(),
                entity.getPricePerDay(),
                entity.getStatus(),
                entity.getImageUrl(),
                entity.getOwnerId(),
                entity.getCreatedAt()
        );
    }
}
