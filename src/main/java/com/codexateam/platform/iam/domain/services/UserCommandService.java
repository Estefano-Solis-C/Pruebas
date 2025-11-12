package com.codexateam.platform.iam.domain.services;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.commands.SignInCommand;
import com.codexateam.platform.iam.domain.model.commands.SignUpCommand;

import java.util.Optional;

/**
 * Service interface for handling User commands (SignUp, SignIn).
 */
public interface UserCommandService {
    Optional<User> handle(SignUpCommand command);
    Optional<User> handle(SignInCommand command);
}
