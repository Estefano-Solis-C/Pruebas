package com.codexateam.platform.iam.domain.services;

import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.queries.GetRoleByNameQuery;
import java.util.Optional;

/**
 * Service interface for handling Role queries.
 */
public interface RoleQueryService {
    Optional<Role> handle(GetRoleByNameQuery query);
}
