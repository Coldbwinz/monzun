package com.example.monzun.repositories;

import com.example.monzun.entities.User;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingRepositoryWithJOOQ {
    Iterable<Long> getTrackerTrackingIds(User user);
    Iterable<Long> getStartupTrackingIds(User user);
}
