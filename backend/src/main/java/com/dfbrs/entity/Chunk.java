package com.dfbrs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Physical storage chunk identified by its SHA‑256 hash.
 */
@Entity
@Table(name = "chunks", uniqueConstraints = @UniqueConstraint(columnNames = {"hash"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hash", nullable = false, length = 64, columnDefinition = "VARCHAR(64)", unique = true)
    private String hash; // hex string of SHA‑256

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "path", nullable = false)
    private String path; // absolute path on disk

    @Column(name = "reference_count", nullable = false)
    private Long referenceCount;
}
