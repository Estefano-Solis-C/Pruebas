package com.codexateam.platform.listings.application.internal.commandservices;

import com.codexateam.platform.listings.domain.model.aggregates.Vehicle;
import com.codexateam.platform.listings.domain.model.commands.CreateVehicleCommand;
import com.codexateam.platform.listings.domain.services.VehicleCommandService;
import com.codexateam.platform.listings.infrastructure.persistence.jpa.repositories.VehicleRepository;
// TODO: Inject an IAM ACL (Anti-Corruption Layer) facade to validate ownerId
// import com.codexateam.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private final VehicleRepository vehicleRepository;
    // private final IamContextFacade iamContextFacade;

    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Optional<Vehicle> handle(CreateVehicleCommand command) {
        // TODO: Validate ownerId using ACL
        // if (!iamContextFacade.existsUserByIdAndRole(command.ownerId(), Roles.ROLE_ARRENDADOR)) {
        //    throw new IllegalArgumentException("Owner does not exist or is not an ARRENDADOR");
        // }
        
        var vehicle = new Vehicle(command);
        try {
            vehicleRepository.save(vehicle);
            return Optional.of(vehicle);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
