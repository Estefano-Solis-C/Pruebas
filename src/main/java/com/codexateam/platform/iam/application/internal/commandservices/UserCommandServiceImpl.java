package com.codexateam.platform.iam.application.internal.commandservices;

import com.codexateam.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.commands.SignInCommand;
import com.codexateam.platform.iam.domain.model.commands.SignUpCommand;
import com.codexateam.platform.iam.domain.services.UserCommandService;
import com.codexateam.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of UserCommandService.
 * Handles the logic for user sign-up and sign-in.
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final HashingService hashingService;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
    }

    /**
     * Handles the SignUpCommand.
     * Validates if the email already exists, hashes the password, and saves the new user.
     */
    @Override
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("User with email " + command.email() + " already exists");
        }
        var user = new User(command.name(), command.email(), hashingService.encode(command.password()), command.roles());
        userRepository.save(user);
        return Optional.of(user);
    }

    /**
     * Handles the SignInCommand.
     * Finds the user by email and validates the password.
     */
    @Override
    public Optional<User> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        return user;
    }
}
