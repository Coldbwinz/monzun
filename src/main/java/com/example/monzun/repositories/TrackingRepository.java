package com.example.monzun.repositories;

import com.example.monzun.entities.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long>, TrackingRepositoryWithJOOQ {
    List<Tracking> findByActiveTrueAndStartedAtAfter(LocalDateTime startedAt);
}
