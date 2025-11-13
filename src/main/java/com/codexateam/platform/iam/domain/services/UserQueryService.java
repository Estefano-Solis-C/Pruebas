package com.codexateam.platform.iam.domain.services;

import java.util.List;
import java.util.Optional;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.codexateam.platform.iam.domain.model.queries.GetUserByIdQuery;

public interface UserQueryService {
    List<User> handle(GetAllUsersQuery query);
    Optional<User> handle(GetUserByIdQuery query);
}
