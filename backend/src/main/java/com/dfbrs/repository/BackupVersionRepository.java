package com.dfbrs.repository;

import com.dfbrs.entity.BackupVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupVersionRepository extends JpaRepository<BackupVersion, Long> {
    List<BackupVersion> findByBackupFileIdOrderByVersionNumberDesc(Long backupFileId);
    int countByBackupFileId(Long backupFileId);
}
