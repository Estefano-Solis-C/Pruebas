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

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<List<UserResource>> getAllUsers() {
        var users = userQueryService.handle(new GetAllUsersQuery());
        var resources = users.stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResource>> getAllUsersAlt() {
        var users = userQueryService.handle(new GetAllUsersQuery());
        var resources = users.stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<UserResource> updatePassword(@PathVariable Long userId, @RequestBody UpdatePasswordResource resource) {
        var command = UpdatePasswordCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var updated = userCommandService.handle(command)
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{userId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<UserResource> updateUser(@PathVariable Long userId, @RequestBody UpdateUserResource resource) {
        var command = UpdateUserCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var updated = userCommandService.handle(command)
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(updated);
    }
}
