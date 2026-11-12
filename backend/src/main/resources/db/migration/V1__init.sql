-- Flyway migration V1: schema initialization for DFBRS
-- Users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- Chunks (content-addressable storage)
CREATE TABLE chunks (
    id BIGSERIAL PRIMARY KEY,
    hash VARCHAR(64) NOT NULL UNIQUE,
    size_bytes BIGINT NOT NULL,
    path TEXT NOT NULL,
    reference_count BIGINT NOT NULL CHECK (reference_count >= 0)
);

-- Logical backup files owned by a user
CREATE TABLE backup_files (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    original_name VARCHAR(255) NOT NULL,
    size_bytes BIGINT NOT NULL,
    content_type VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Versions of a logical file
CREATE TABLE backup_versions (
    id BIGSERIAL PRIMARY KEY,
    backup_file_id BIGINT NOT NULL REFERENCES backup_files(id) ON DELETE CASCADE,
    version_number INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    UNIQUE (backup_file_id, version_number)
);

-- Manifest entries linking versions to ordered chunks
CREATE TABLE backup_manifest_entries (
    id BIGSERIAL PRIMARY KEY,
    backup_version_id BIGINT NOT NULL REFERENCES backup_versions(id) ON DELETE CASCADE,
    chunk_hash VARCHAR(64) NOT NULL,
    order_index INT NOT NULL,
    CONSTRAINT fk_chunk FOREIGN KEY (chunk_hash) REFERENCES chunks(hash)
);

-- Indexes for fast look‑ups
CREATE INDEX idx_chunks_hash ON chunks(hash);
CREATE INDEX idx_backup_files_user ON backup_files(user_id);
CREATE INDEX idx_backup_versions_file ON backup_versions(backup_file_id);
CREATE INDEX idx_manifest_version ON backup_manifest_entries(backup_version_id);
