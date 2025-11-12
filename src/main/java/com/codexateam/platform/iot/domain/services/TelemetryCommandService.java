package com.codexateam.platform.iot.domain.services;

import com.codexateam.platform.iot.domain.model.aggregates.Telemetry;
import com.codexateam.platform.iot.domain.model.commands.RecordTelemetryCommand;
import java.util.Optional;

/**
 * Service interface for handling Telemetry commands.
 */
public interface TelemetryCommandService {
    /**
     * Handles the RecordTelemetryCommand.
     * @param command The command to record telemetry data.
     * @return An Optional containing the created Telemetry aggregate.
     */
    Optional<Telemetry> handle(RecordTelemetryCommand command);
}
