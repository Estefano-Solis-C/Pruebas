package com.codexateam.platform.iam.domain.services;

import com.codexateam.platform.iam.domain.model.commands.SeedRolesCommand;

/**
 * Service interface for handling Role commands.
 */
public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}
