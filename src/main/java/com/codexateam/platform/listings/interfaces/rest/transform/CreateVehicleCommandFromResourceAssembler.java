package com.codexateam.platform.listings.interfaces.rest.transform;

import com.codexateam.platform.listings.domain.model.commands.CreateVehicleCommand;
import com.codexateam.platform.listings.interfaces.rest.resources.CreateVehicleResource;

public class CreateVehicleCommandFromResourceAssembler {
    public static CreateVehicleCommand toCommandFromResource(CreateVehicleResource resource, Long ownerId) {
        return new CreateVehicleCommand(
                resource.brand(),
                resource.model(),
                resource.year(),
                resource.pricePerDay(),
                resource.imageUrl(),
                ownerId // Injected from security context
        );
    }
}
