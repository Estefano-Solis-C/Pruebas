package com.codexateam.platform.iam.domain.services;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.commands.SignInCommand;
import com.codexateam.platform.iam.domain.model.commands.SignUpCommand;
import com.codexateam.platform.iam.domain.model.commands.UpdatePasswordCommand;
import com.codexateam.platform.iam.domain.model.commands.UpdateUserCommand;
import com.codexateam.platform.iam.domain.model.commands.DeleteUserCommand;

import java.util.Optional;

/**
 * Service interface for handling User commands (SignUp, SignIn, Update...).
 */
public interface UserCommandService {
    Optional<User> handle(SignUpCommand command);
    Optional<User> handle(SignInCommand command);
    Optional<User> handle(UpdateUserCommand command);
    Optional<User> handle(UpdatePasswordCommand command);
    void handle(DeleteUserCommand command);
}
