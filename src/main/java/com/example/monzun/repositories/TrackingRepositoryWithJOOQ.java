package com.example.monzun.repositories;

import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingRepositoryWithJOOQ {
    List<Tracking> getTrackerTrackings(User user);
    List<Tracking> getStartupTrackings(User user);
}
