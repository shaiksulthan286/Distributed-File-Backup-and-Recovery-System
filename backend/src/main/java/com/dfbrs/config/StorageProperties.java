package com.dfbrs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the chunk storage engine.
 * The base directory can be overridden via the environment variable
 * STORAGE_BASE_DIR or the application.yml property `storage.base-dir`.
 */
@ConfigurationProperties(prefix = "storage")
public record StorageProperties(String baseDir, long chunkSize) {
    public StorageProperties {
        if (baseDir == null || baseDir.isBlank()) {
            throw new IllegalArgumentException("storage.base-dir must be set");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("storage.chunk-size must be positive");
        }
    }
}
