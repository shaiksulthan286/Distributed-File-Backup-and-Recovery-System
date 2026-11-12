package com.dfbrs.repository;

import com.dfbrs.entity.BackupManifestEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupManifestEntryRepository extends JpaRepository<BackupManifestEntry, Long> {
    List<BackupManifestEntry> findByBackupVersionIdOrderByOrderIndex(Long backupVersionId);
}
