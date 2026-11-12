package com.dfbrs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Manifest entry linking a {@link BackupVersion} to an ordered chunk.
 */
@Entity
@Table(name = "backup_manifest_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupManifestEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backup_version_id", nullable = false)
    private BackupVersion backupVersion;

    @Column(name = "chunk_hash", nullable = false, length = 64, columnDefinition = "VARCHAR(64)")
    private String chunkHash;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}
