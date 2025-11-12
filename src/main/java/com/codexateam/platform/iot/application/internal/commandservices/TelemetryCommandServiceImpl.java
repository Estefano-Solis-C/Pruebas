package com.codexateam.platform.iot.application.internal.commandservices;

import com.codexateam.platform.iot.domain.model.aggregates.Telemetry;
import com.codexateam.platform.iot.domain.model.commands.RecordTelemetryCommand;
import com.codexateam.platform.iot.domain.services.TelemetryCommandService;
import com.codexateam.platform.iot.infrastructure.persistence.jpa.repositories.TelemetryRepository;
// TODO: Inject Listings ACL Facade
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of TelemetryCommandService.
 */
@Service
public class TelemetryCommandServiceImpl implements TelemetryCommandService {

    private final TelemetryRepository telemetryRepository;
    // private final ExternalListingsService externalListingsService; // ACL

    public TelemetryCommandServiceImpl(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    /**
     * Handles the RecordTelemetryCommand.
     * TODO: Add validation:
     * 1. Use Listings ACL to verify vehicleId exists.
     * 2. Verify that the authenticated principal (device/user) has permission to post data for this vehicle.
     */
    @Override
    public Optional<Telemetry> handle(RecordTelemetryCommand command) {
        // if (!externalListingsService.vehicleExists(command.vehicleId())) {
        //    throw new IllegalArgumentException("Vehicle not found");
        // }

        var telemetry = new Telemetry(command);
        try {
            telemetryRepository.save(telemetry);
            return Optional.of(telemetry);
        } catch (Exception e) {
            // Log error
            return Optional.empty();
        }
    }
}
