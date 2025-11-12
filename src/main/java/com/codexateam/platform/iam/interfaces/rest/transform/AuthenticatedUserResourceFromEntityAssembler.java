package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

/**
 * Assembler to convert User entity to AuthenticatedUserResource DTO.
 */
public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(user.getId(), user.getEmail(), token);
    }
}
