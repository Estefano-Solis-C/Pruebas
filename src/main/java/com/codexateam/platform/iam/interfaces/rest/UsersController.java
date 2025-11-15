package com.codexateam.platform.iam.interfaces.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codexateam.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.codexateam.platform.iam.domain.services.UserQueryService;
import com.codexateam.platform.iam.domain.services.UserCommandService;
import com.codexateam.platform.iam.interfaces.rest.resources.UserResource;
import com.codexateam.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.codexateam.platform.iam.interfaces.rest.resources.UpdateUserResource;
import com.codexateam.platform.iam.interfaces.rest.resources.UpdatePasswordResource;
import com.codexateam.platform.iam.interfaces.rest.transform.UpdateUserCommandFromResourceAssembler;
import com.codexateam.platform.iam.interfaces.rest.transform.UpdatePasswordCommandFromResourceAssembler;
import com.codexateam.platform.iam.domain.model.commands.DeleteUserCommand;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints for managing user profiles")
public class UsersController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    @Operation(summary = "Get All Users", description = "Get a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found")
    })
    @GetMapping
    public ResponseEntity<List<UserResource>> getAllUsers() {
        var users = userQueryService.handle(new GetAllUsersQuery());
        var resources = users.stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Update User Password", description = "Update the authenticated user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or incorrect current password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}/password")
    public ResponseEntity<UserResource> updatePassword(@PathVariable Long userId, @RequestBody UpdatePasswordResource resource) {
        var command = UpdatePasswordCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var updated = userCommandService.handle(command)
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Update User Profile", description = "Update the authenticated user's name and email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResource> updateUser(@PathVariable Long userId, @RequestBody UpdateUserResource resource) {
        var command = UpdateUserCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var updated = userCommandService.handle(command)
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete User", description = "Delete a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userCommandService.handle(new DeleteUserCommand(userId));
        return ResponseEntity.ok().build();
    }
}