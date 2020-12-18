package com.example.monzun.repositories;

import com.example.monzun.entities.Startup;
import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.TrackingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingRequestRepository extends JpaRepository<TrackingRequest, Long> {
    Optional<TrackingRequest> findByStartupAndTracking(Startup startup, Tracking tracking);
    List<TrackingRequest> findByStartup(Startup startup);
}
