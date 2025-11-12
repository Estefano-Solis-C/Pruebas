package com.codexateam.platform.listings.application.internal.commandservices;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.codexateam.platform.iam.domain.services.UserQueryService;
import com.codexateam.platform.listings.domain.model.aggregates.Vehicle;
import com.codexateam.platform.listings.domain.model.commands.CreateVehicleCommand;
import com.codexateam.platform.listings.infrastructure.persistence.jpa.repositories.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleCommandServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private VehicleCommandServiceImpl service;

    @Test
    void shouldThrowWhenOwnerMissing() {
        when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.empty());
        var cmd = new CreateVehicleCommand("Brand","Model",2024,100.0,null,999L);
        var ex = assertThrows(IllegalArgumentException.class, () -> service.handle(cmd));
        assertTrue(ex.getMessage().contains("Owner no existe"));
    }

    @Test
    void shouldThrowWhenOwnerNotArrendador() {
        var user = new User("Name","email@test.com","pwd");
        user.addRole(new Role(Roles.ROLE_ARRENDATARIO));
        when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.of(user));
        var cmd = new CreateVehicleCommand("Brand","Model",2024,100.0,null,999L);
        var ex = assertThrows(IllegalArgumentException.class, () -> service.handle(cmd));
        assertTrue(ex.getMessage().contains("Owner no tiene el rol requerido"));
    }

    @Test
    void shouldSaveWhenValidOwner() {
        var user = new User("Name","email@test.com","pwd");
        user.addRole(new Role(Roles.ROLE_ARRENDADOR));
        when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.of(user));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new CreateVehicleCommand("Brand","Model",2024,100.0,null,999L);
        var result = service.handle(cmd);
        assertTrue(result.isPresent());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }
}
