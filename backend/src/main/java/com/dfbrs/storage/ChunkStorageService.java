package com.dfbrs.storage;

import com.dfbrs.config.StorageProperties;
import com.dfbrs.entity.Chunk;
import com.dfbrs.repository.ChunkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Handles low‑level chunk storage on the local filesystem and maintains
 * reference counts in the {@link Chunk} table.
 */
@Service
@RequiredArgsConstructor
public class ChunkStorageService {

    private final StorageProperties storageProperties;
    private final ChunkRepository chunkRepository;

    private static final HexFormat HEX = HexFormat.of();

    /**
     * Stores the provided {@code InputStream} as a chunk.
     * If a chunk with the same SHA‑256 hash already exists, its reference count is incremented
     * and the existing file is reused.
     *
     * @param inputStream the data of the chunk (must be fully read by this method)
     * @return the persisted {@link Chunk} entity representing the stored chunk
     */
    @Transactional
    public Chunk storeChunk(InputStream inputStream) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Compute hash while streaming to a temporary file
            Path tempFile = Files.createTempFile("chunk-", ".tmp");
            try (DigestInputStream dis = new DigestInputStream(inputStream, digest)) {
                Files.copy(dis, tempFile);
            }
            byte[] hashBytes = digest.digest();
            String hashHex = HEX.formatHex(hashBytes);

            // Check for existing chunk
            return chunkRepository.findByHash(hashHex)
                    .map(existing -> {
                        // Increment reference count
                        existing.setReferenceCount(existing.getReferenceCount() + 1);
                        return chunkRepository.save(existing);
                    })
                    .orElseGet(() -> {
                        // Move temp file to final location
                        Path finalPath = Path.of(storageProperties.baseDir(), hashHex);
                        try {
                            Files.createDirectories(finalPath.getParent());
                            Files.move(tempFile, finalPath);
                            Chunk chunk = Chunk.builder()
                                    .hash(hashHex)
                                    .sizeBytes(Files.size(finalPath))
                                    .path(finalPath.toString())
                                    .referenceCount(1L)
                                    .build();
                            return chunkRepository.save(chunk);
                        } catch (IOException e) {
                            // cleanup temp file on failure
                            try { Files.deleteIfExists(tempFile); } catch (IOException ignored) {}
                            throw new RuntimeException("Failed to persist chunk to storage", e);
                        }
                    });
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
