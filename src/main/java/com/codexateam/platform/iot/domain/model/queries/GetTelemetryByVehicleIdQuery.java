package com.codexateam.platform.iot.domain.model.queries;

/**
 * Query to find all telemetry data for a specific vehicle.
 *
 */
public record GetTelemetryByVehicleIdQuery(Long vehicleId) {
}
