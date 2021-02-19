package com.example.monzun.repositories.impl;

import com.example.monzun.entities.User;
import com.example.monzun.repositories.TrackingRepositoryWithJOOQ;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TrackingRepositoryWithJOOQImpl implements TrackingRepositoryWithJOOQ {
    private final JdbcTemplate jdbcTemplate;

    public TrackingRepositoryWithJOOQImpl(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<Long> getTrackerTrackingIds(User user) {
        return jdbcTemplate.queryForList("SELECT DISTINCT(t.tracking_id) " +
                "FROM trackings AS t " +
                "JOIN startup_trackings AS st " +
                "ON st.tracking_id = t.tracking_id AND st.tracker_id = " + user.getId(), Long.class);
    }

    @Override
    public Iterable<Long> getStartupTrackingIds(User user) {
        return jdbcTemplate.queryForList("SELECT DISTINCT(t.tracking_id) " +
                "FROM trackings AS t " +
                "JOIN startups AS s " +
                "ON s.owner_id = " + user.getId() + " " +
                "JOIN startup_trackings AS st " +
                "ON st.startup_id = s.startup_id " +
                "AND st.tracking_id = t.tracking_id", Long.class);
    }
}
