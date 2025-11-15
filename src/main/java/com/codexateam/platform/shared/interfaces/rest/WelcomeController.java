package com.codexateam.platform.shared.interfaces.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * Welcome controller for the root endpoint.
 * Provides information about available API endpoints.
 */
@Tag(name = "Welcome", description = "API Welcome Endpoint")
@RestController
@RequestMapping("/")
public class WelcomeController {

    @GetMapping
    @Operation(summary = "API Welcome Message", description = "Provides information and available endpoints of the API")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public Map<String, Object> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to CodexaTeam Backend API");
        response.put("status", "running");
        response.put("version", "1.0.0");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("API Documentation", "/swagger-ui.html");
        endpoints.put("API Docs JSON", "/v3/api-docs");
        endpoints.put("Bookings", "/api/v1/bookings");
        endpoints.put("Vehicles", "/api/v1/vehicles");
        endpoints.put("Reviews", "/api/v1/reviews");
        endpoints.put("Telemetry", "/api/v1/telemetry");
        endpoints.put("Users", "/api/v1/users");
        endpoints.put("Authentication", "/api/v1/authentication");

        response.put("endpoints", endpoints);

        Map<String, String> authInfo = new HashMap<>();
        authInfo.put("status", "JWT Authentication is ENABLED");
        authInfo.put("login", "POST /api/v1/authentication/sign-in");
        authInfo.put("register", "POST /api/v1/authentication/sign-up");
        authInfo.put("note", "Include 'Authorization: Bearer <token>' header for protected endpoints");

        response.put("authentication", authInfo);

        return response;
    }
}
