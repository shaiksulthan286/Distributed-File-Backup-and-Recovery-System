package com.dfbrs.repository;

import com.dfbrs.entity.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {
    Optional<Chunk> findByHash(String hash);
}
