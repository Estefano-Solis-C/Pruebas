package com.codexateam.platform.iam.interfaces.rest;

import com.codexateam.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.codexateam.platform.iam.domain.model.queries.GetRoleByNameQuery;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.domain.services.RoleQueryService;
import com.codexateam.platform.iam.domain.services.UserCommandService;
import com.codexateam.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.codexateam.platform.iam.interfaces.rest.resources.SignInResource;
import com.codexateam.platform.iam.interfaces.rest.resources.SignUpResource;
import com.codexateam.platform.iam.interfaces.rest.resources.UserResource;
import com.codexateam.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.codexateam.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.codexateam.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.codexateam.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * REST Controller for Authentication endpoints (Sign-Up, Sign-In).
 */
@RestController
@RequestMapping("/api/v1/authentication")
@Tag(name = "Authentication", description = "Endpoints for user authentication (Sign-Up, Sign-In)")
public class AuthenticationController {

    private final UserCommandService userCommandService;
    private final RoleQueryService roleQueryService;
    private final TokenService tokenService;

    public AuthenticationController(UserCommandService userCommandService, RoleQueryService roleQueryService, TokenService tokenService) {
        this.userCommandService = userCommandService;
        this.roleQueryService = roleQueryService;
        this.tokenService = tokenService;
    }

    /**
     * Handles the POST request for user sign-up.
     * @param resource The sign-up data (name, email, password, role).
     * @return A ResponseEntity with the created UserResource or an error.
     */
    @PostMapping("/sign-up")
    public ResponseEntity<UserResource> signUp(@RequestBody SignUpResource resource) {
        // Convert role string from db.json ('arrendador') to Enum ('ROLE_ARRENDADOR')
        Roles roleEnum;
        if ("arrendador".equalsIgnoreCase(resource.role())) {
            roleEnum = Roles.ROLE_ARRENDADOR;
        } else if ("arrendatario".equalsIgnoreCase(resource.role())) {
            roleEnum = Roles.ROLE_ARRENDATARIO;
        } else {
            throw new IllegalArgumentException("Invalid role provided. Must be 'arrendador' or 'arrendatario'.");
        }
        
        var role = roleQueryService.handle(new GetRoleByNameQuery(roleEnum))
                .orElseThrow(() -> new RuntimeException("Role not found: " + resource.role()));

        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource, Set.of(role));
        var user = userCommandService.handle(command)
                .orElseThrow(() -> new RuntimeException("Error creating user"));
        
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResource);
    }

    /**
     * Handles the POST request for user sign-in.
     * @param resource The sign-in data (email, password).
     * @return A ResponseEntity with the AuthenticatedUserResource (including token) or an error.
     */
    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticatedUserResource> signIn(@RequestBody SignInResource resource) {
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        var user = userCommandService.handle(command)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        var token = tokenService.generateToken(user.getEmail());
        var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(user, token);
        return ResponseEntity.ok(authenticatedUserResource);
    }
}
