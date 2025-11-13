package com.codexateam.platform.listings.domain.model.commands;

/**
 * Command to update a vehicle's rental status.
 */
public record UpdateVehicleStatusCommand(Long vehicleId, String status) {
}

