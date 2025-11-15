package com.codexateam.platform.iot.interfaces.rest.resources;

import java.util.Date;

/**
 * Represents a telemetry data resource.
 * This record is used to transfer telemetry data.
 * @param id The unique identifier of the telemetry record.
 * @param vehicleId The identifier of the vehicle.
 * @param latitude The latitude of the vehicle's location.
 * @param longitude The longitude of the vehicle's location.
 * @param speed The speed of the vehicle.
 * @param fuelLevel The fuel level of the vehicle.
 * @param timestamp The timestamp of the telemetry data.
 */
public record TelemetryResource(
        Long id,
        Long vehicleId,
        Double latitude,
        Double longitude,
        Double speed,
        Double fuelLevel,
        Date timestamp
) {
}
