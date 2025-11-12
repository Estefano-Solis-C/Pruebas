package com.codexateam.platform.iam.interfaces.rest;

import com.codexateam.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.codexateam.platform.iam.domain.services.UserQueryService;
import com.codexateam.platform.iam.interfaces.rest.resources.UserResource;
import com.codexateam.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for User queries.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints for querying user data")
public class UsersController {

    private final UserQueryService userQueryService;

    public UsersController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    /**
     * Handles the GET request to find a user by their ID.
     * @param userId The ID of the user.
     * @return A ResponseEntity with the UserResource or Not Found.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResource> getUserById(@PathVariable Long userId) {
        var query = new GetUserByIdQuery(userId);
        var user = userQueryService.handle(query)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user);
        return ResponseEntity.ok(userResource);
    }
}
