package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

import java.util.stream.Collectors;

/**
 * Assembler to convert User entity to AuthenticatedUserResource DTO.
 */
public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        // Extrae los roles del objeto User y conviértelos a Set<String>
        var roles = user.getRoles().stream()
                .map(role -> role.getStringName())
                .collect(Collectors.toSet());

        // Llama al nuevo constructor con el nombre y los roles
        return new AuthenticatedUserResource(user.getId(), user.getEmail(), user.getName(), roles, token);
    }
}