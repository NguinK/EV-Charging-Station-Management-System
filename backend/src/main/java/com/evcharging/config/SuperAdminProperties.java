package com.evcharging.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Super Admin account initialization.
 * Reads from application.properties with prefix "app.super-admin"
 */
@Component
@ConfigurationProperties(prefix = "app.super-admin")
@Data
public class SuperAdminProperties {

    /**
     * Email for the super admin account
     */
    private String email;

    /**
     * Password for the super admin account
     * Should be loaded from environment variable in production
     */
    private String password;

    /**
     * Full name for the super admin account
     */
    private String fullName;

    /**
     * Whether super admin initialization is enabled
     * Set to false to disable auto-creation
     */
    private boolean enabled = true;
}

