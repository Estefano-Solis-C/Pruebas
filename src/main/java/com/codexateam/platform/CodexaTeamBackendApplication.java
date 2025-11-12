package com.codexateam.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the CodexaTeam Backend application.
 * Enables JPA Auditing to automatically manage CreatedAt and UpdatedAt fields.
 */
@SpringBootApplication
@EnableJpaAuditing
public class CodexaTeamBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodexaTeamBackendApplication.class, args);
    }

}
