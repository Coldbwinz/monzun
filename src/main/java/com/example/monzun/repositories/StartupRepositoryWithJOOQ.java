package com.example.monzun.repositories;

import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.Startup;
import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StartupRepositoryWithJOOQ {
    List<Startup> getTrackerStartups(User user);

    Iterable<Long> getTrackerStartupsOnTrackingIds(User user, Tracking tracking);

    List<Startup> getStartupStartups(User user);

    List<User> getStartupTrackers(Startup startup);

    List<Attachment> getStartupAttachments(Startup startup);
}
