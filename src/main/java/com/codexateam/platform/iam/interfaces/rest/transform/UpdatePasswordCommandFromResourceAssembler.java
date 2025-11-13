package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.domain.model.commands.UpdatePasswordCommand;
import com.codexateam.platform.iam.interfaces.rest.resources.UpdatePasswordResource;

/**
 * Assembler to convert UpdatePasswordResource to UpdatePasswordCommand.
 */
public class UpdatePasswordCommandFromResourceAssembler {
    public static UpdatePasswordCommand toCommandFromResource(Long userId, UpdatePasswordResource resource) {
        return new UpdatePasswordCommand(userId, resource.currentPassword(), resource.newPassword());
    }
}
