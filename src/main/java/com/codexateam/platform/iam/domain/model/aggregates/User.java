package com.codexateam.platform.iam.domain.model.aggregates;

import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the User aggregate root in the IAM bounded context.
 * Based on the 'users' table in db.json.
 * Extends AuditableAbstractAggregateRoot to include auditing fields.
 */
@NoArgsConstructor
@Getter
@Entity
@Table(name = "users") // Matches 'db.json'
public class User extends AuditableAbstractAggregateRoot<User> {

    /**
     * The user's full name.
     * Based on 'name' from db.json.
     */
    @NotBlank
    @Size(max = 100)
    @Setter
    private String name;
    
    /**
     * The user's email address. Must be unique.
     * Based on 'email' from db.json.
     */
    @NotBlank
    @Size(max = 50)
    @Email
    @Column(unique = true)
    @Setter
    private String email;

    /**
     * The user's hashed password.
     * Based on 'password' from db.json.
     */
    @NotBlank
    @Size(max = 120)
    @Setter
    private String password;

    /**
     * A set of roles assigned to the user.
     * Based on 'role' from db.json.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, Set<Role> roles) {
        this(name, email, password);
        this.roles.addAll(roles);
    }

    /**
     * Adds a role to the user's set of roles.
     * @param role The Role entity to add.
     * @return The current User instance.
     */
    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }
}
