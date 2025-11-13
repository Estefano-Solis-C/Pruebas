package com.codexateam.platform.listings.interfaces.acl;

import com.codexateam.platform.listings.domain.model.aggregates.Vehicle;
import com.codexateam.platform.listings.domain.model.queries.GetVehicleByIdQuery;
import com.codexateam.platform.listings.domain.services.VehicleQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.codexateam.platform.listings.domain.model.commands.UpdateVehicleStatusCommand;
import com.codexateam.platform.listings.domain.services.VehicleCommandService;

@Service
public class ListingsContextFacade {

    private final VehicleQueryService vehicleQueryService;
    private final VehicleCommandService vehicleCommandService;

    public ListingsContextFacade(VehicleQueryService vehicleQueryService, VehicleCommandService vehicleCommandService) {
        this.vehicleQueryService = vehicleQueryService;
        this.vehicleCommandService = vehicleCommandService;
    }

    /**
     * Usado por 'iot' para verificar si un vehículo existe.
     */
    @SuppressWarnings("unused")
    public boolean existsVehicleById(Long vehicleId) {
        var query = new GetVehicleByIdQuery(vehicleId);
        return vehicleQueryService.handle(query).isPresent();
    }

    /**
     * Usado por 'booking' para obtener el precio de un vehículo.
     * Devuelve el precio por día (pricePerDay).
     */
    public Optional<Double> getVehiclePriceById(Long vehicleId) {
        var query = new GetVehicleByIdQuery(vehicleId);
        var vehicle = vehicleQueryService.handle(query);
        return vehicle.map(Vehicle::getPricePerDay);
    }

    /**
     * Usado por 'booking' para actualizar el estado de un vehículo.
     */
    public Optional<Vehicle> updateVehicleStatus(Long vehicleId, String status) {
        var command = new UpdateVehicleStatusCommand(vehicleId, status);
        return vehicleCommandService.handle(command);
    }
}
