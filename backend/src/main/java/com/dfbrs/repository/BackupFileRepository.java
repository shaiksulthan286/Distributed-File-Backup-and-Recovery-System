package com.dfbrs.repository;

import com.dfbrs.entity.BackupFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupFileRepository extends JpaRepository<BackupFile, Long> {
    List<BackupFile> findByOwnerId(Long ownerId);
}
