package com.example.monzun.repositories;

import com.example.monzun.entities.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Attachment findByuuid(UUID uuid);
    boolean existsAllByUuid(UUID uuid);
}
