package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.domain.model.commands.SignUpCommand;
import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.interfaces.rest.resources.SignUpResource;

import java.util.Set;

/**
 * Assembler to convert SignUpResource DTO to SignUpCommand.
 */
public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource resource, Set<Role> roles) {
        return new SignUpCommand(resource.name(), resource.email(), resource.password(), roles);
    }
}
