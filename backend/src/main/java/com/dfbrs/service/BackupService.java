package com.dfbrs.service;

import com.dfbrs.entity.*;
import com.dfbrs.repository.*;
import com.dfbrs.storage.ChunkStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.dfbrs.config.StorageProperties;

/**
 * Core backup workflow: chunk a file, store unique chunks, create a manifest and version record.
 */
@Service
@RequiredArgsConstructor
public class BackupService {

    private final BackupFileRepository backupFileRepository;
    private final BackupVersionRepository backupVersionRepository;
    private final BackupManifestEntryRepository manifestEntryRepository;
    private final ChunkStorageService chunkStorageService;
    private final com.dfbrs.config.StorageProperties storageProperties;

    /**
     * Performs a backup for the given user and uploaded file.
     * Returns the created {@link BackupVersion} (including version number).
     */
    @Transactional
    public BackupVersion backupFile(User user, MultipartFile file) throws IOException {
        // 1️⃣ Find or create the logical BackupFile record
        BackupFile backupFile = backupFileRepository.findByOwnerId(user.getId()).stream()
                .filter(bf -> bf.getOriginalName().equals(file.getOriginalFilename()))
                .findFirst()
                .orElseGet(() -> {
                    BackupFile bf = BackupFile.builder()
                            .owner(user)
                            .originalName(file.getOriginalFilename())
                            .sizeBytes(file.getSize())
                            .contentType(file.getContentType())
                            .createdAt(Instant.now())
                            .build();
                    return backupFileRepository.save(bf);
                });

        // Determine next version number
        int nextVersion = backupVersionRepository.countByBackupFileId(backupFile.getId()) + 1;

        // 2️⃣ Create version record
        BackupVersion version = BackupVersion.builder()
                .backupFile(backupFile)
                .versionNumber(nextVersion)
                .createdAt(Instant.now())
                .build();
        version = backupVersionRepository.save(version);

        // 3️⃣ Process the file stream into chunks
        List<BackupManifestEntry> manifestEntries = new ArrayList<>();
        long configuredChunkSize = storageProperties.chunkSize();
        try (InputStream is = file.getInputStream()) {
            int order = 0;
            byte[] buffer = new byte[(int) configuredChunkSize];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                // Feed only the bytes that were actually read to the storage service
                java.io.ByteArrayInputStream chunkStream = new java.io.ByteArrayInputStream(buffer, 0, bytesRead);
                com.dfbrs.entity.Chunk storedChunk = chunkStorageService.storeChunk(chunkStream);
                BackupManifestEntry entry = BackupManifestEntry.builder()
                        .backupVersion(version)
                        .chunkHash(storedChunk.getHash())
                        .orderIndex(order++)
                        .build();
                manifestEntries.add(entry);
            }
        }
        // Persist manifest entries (link them to the version)
        manifestEntryRepository.saveAll(manifestEntries);
        version.setManifestEntries(manifestEntries);
        backupVersionRepository.save(version);
        return version;
    }
}
