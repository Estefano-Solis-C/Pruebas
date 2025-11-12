package com.codexateam.platform.iam.domain.services;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.codexateam.platform.iam.domain.model.queries.GetUserByIdQuery;

import java.util.Optional;

/**
 * Service interface for handling User queries.
 */
public interface UserQueryService {
    Optional<User> handle(GetUserByIdQuery query);
    Optional<User> handle(GetUserByEmailQuery query);
}
