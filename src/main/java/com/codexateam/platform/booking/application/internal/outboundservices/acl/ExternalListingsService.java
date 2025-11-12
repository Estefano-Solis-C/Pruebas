package com.codexateam.platform.booking.application.internal.outboundservices.acl;

import com.codexateam.platform.listings.interfaces.rest.VehiclesController;
import com.codexateam.platform.listings.interfaces.rest.resources.VehicleResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Anti-Corruption Layer (ACL) service to communicate with the Listings bounded context.
 * This class fetches data (like vehicle price and owner) from the Listings module
 * without the Booking module needing to know about Listings' internal domain model.
 */
@Service
public class ExternalListingsService {

    private final VehiclesController vehiclesController;

    public ExternalListingsService(VehiclesController vehiclesController) {
        this.vehiclesController = vehiclesController;
    }

    /**
     * Fetches the details of a vehicle by its ID.
     * @param vehicleId The ID of the vehicle.
     * @return An Optional containing the VehicleResource if found.
     */
    public Optional<VehicleResource> fetchVehicleById(Long vehicleId) {
        try {
            // This is a direct method call, which is possible in a monolith.
            // In a microservice, this would be an HTTP or gRPC call.
            var response = vehiclesController.getVehicleById(vehicleId);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(response.getBody());
            }
        } catch (Exception e) {
            // Log the error
            return Optional.empty();
        }
        return Optional.empty();
    }
}
