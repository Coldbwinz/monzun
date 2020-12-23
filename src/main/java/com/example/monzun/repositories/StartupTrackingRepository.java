package com.example.monzun.repositories;

import com.example.monzun.entities.Startup;
import com.example.monzun.entities.StartupTracking;
import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StartupTrackingRepository extends JpaRepository<StartupTracking, Long> {
    boolean existsByStartupAndTrackingAndTracker(Startup startup, Tracking tracking, User user);
    Optional<StartupTracking> findByTrackingAndStartup(Tracking tracking, Startup startup);
}
