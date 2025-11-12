package com.codexateam.platform.shared.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * Base abstract class for auditable aggregate roots.
 * Extends Spring Data's AbstractAggregateRoot to support Domain-Driven Design (DDD) event publishing.
 * Includes 'createdAt' and 'updatedAt' timestamps.
 * Also includes 'id' and 'version' fields for persistence.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableAbstractAggregateRoot<T extends AbstractAggregateRoot<T>> extends AbstractAggregateRoot<T> {

    /**
     * The primary key for the aggregate.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The version field for optimistic locking.
     */
    @Version
    private Long version;

    /**
     * Timestamp indicating when the entity was created.
     * It is non-nullable and not updatable.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    /**
     * Timestamp indicating when the entity was last updated.
     * It is non-nullable.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;
}
