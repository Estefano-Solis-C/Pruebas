package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.domain.model.commands.SignInCommand;
import com.codexateam.platform.iam.interfaces.rest.resources.SignInResource;

/**
 * Assembler to convert SignInResource DTO to SignInCommand.
 */
public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.email(), resource.password());
    }
}
