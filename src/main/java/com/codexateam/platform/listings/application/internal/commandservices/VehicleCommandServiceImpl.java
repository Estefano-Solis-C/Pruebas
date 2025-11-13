package com.codexateam.platform.listings.application.internal.commandservices;

import com.codexateam.platform.listings.domain.model.aggregates.Vehicle;
import com.codexateam.platform.listings.domain.model.commands.CreateVehicleCommand;
import com.codexateam.platform.listings.domain.model.commands.UpdateVehicleStatusCommand;
import com.codexateam.platform.listings.domain.services.VehicleCommandService;
import com.codexateam.platform.listings.infrastructure.persistence.jpa.repositories.VehicleRepository;
// TODO: Inject an IAM ACL (Anti-Corruption Layer) facade to validate ownerId
// import com.codexateam.platform.iam.interfaces.acl.IamContextFacade;
import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.domain.services.UserQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private final VehicleRepository vehicleRepository;
    private final UserQueryService userQueryService;
    // private final IamContextFacade iamContextFacade;

    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository, UserQueryService userQueryService) {
        this.vehicleRepository = vehicleRepository;
        this.userQueryService = userQueryService;
    }

    @Override
    public Optional<Vehicle> handle(CreateVehicleCommand command) {
        // Validate ownerId using IAM query service (defense in depth)
        User user = userQueryService.handle(new GetUserByIdQuery(command.ownerId()))
                .orElseThrow(() -> new IllegalArgumentException("Owner no existe"));
        boolean isArrendador = user.getRoles().stream().anyMatch(r -> r.getName() == Roles.ROLE_ARRENDADOR);
        if (!isArrendador) {
            throw new IllegalArgumentException("Owner no tiene el rol requerido (ROLE_ARRENDADOR)");
        }

        var vehicle = new Vehicle(command);
        try {
            vehicleRepository.save(vehicle);
            return Optional.of(vehicle);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Vehicle> handle(UpdateVehicleStatusCommand command) {
        try {
            return vehicleRepository.findById(command.vehicleId())
                    .map(vehicle -> {
                        vehicle.updateStatus(command.status());
                        return vehicleRepository.save(vehicle);
                    });
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
