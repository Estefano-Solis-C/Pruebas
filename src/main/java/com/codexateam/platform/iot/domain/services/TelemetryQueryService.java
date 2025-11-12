package com.codexateam.platform.iot.domain.services;

import com.codexateam.platform.iot.domain.model.aggregates.Telemetry;
import com.codexateam.platform.iot.domain.model.queries.GetTelemetryByVehicleIdQuery;

import java.util.List;

/**
 * Service interface for handling Telemetry queries.
 */
public interface TelemetryQueryService {
    /**
     * Handles the query to get telemetry data by vehicle ID.
     */
    List<Telemetry> handle(GetTelemetryByVehicleIdQuery query);
}
