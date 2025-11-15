package com.codexateam.platform.iot.interfaces.rest.resources;


public record RecordTelemetryResource(
        Long vehicleId,
        Double latitude,
        Double longitude,
        Double speed,
        Double fuelLevel
) {
}
