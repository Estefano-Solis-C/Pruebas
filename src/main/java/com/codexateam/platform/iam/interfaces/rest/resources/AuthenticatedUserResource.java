package com.codexateam.platform.iam.interfaces.rest.resources;

/**
 * DTO for returning an authenticated user's data.
 * @param id The user's ID.
 * @param email The user's email.
 * @param token The JWT token.
 */
public record AuthenticatedUserResource(Long id, String email, String token) {
}
