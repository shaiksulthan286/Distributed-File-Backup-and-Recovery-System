package com.dfbrs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Represents a specific backup version of a {@link BackupFile}.
 * Each version has its own ordered manifest referencing chunks.
 */
@Entity
@Table(name = "backup_versions", uniqueConstraints = @UniqueConstraint(columnNames = {"backup_file_id", "version_number"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backup_file_id", nullable = false)
    private BackupFile backupFile;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "backupVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BackupManifestEntry> manifestEntries;
}
