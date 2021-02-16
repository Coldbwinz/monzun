package com.example.monzun.repositories.impl;

import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.User;
import com.example.monzun.repositories.TrackingRepositoryWithJOOQ;
import org.jooq.DSLContext;

import java.util.List;

public class TrackingRepositoryWithJOOQImpl implements TrackingRepositoryWithJOOQ {
    private final DSLContext jooq;

    public TrackingRepositoryWithJOOQImpl(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Override
    public List<Tracking> getTrackerTrackings(User user) {
        return jooq.fetch("" +
                "SELECT DISTINCT(t.*), a.* " +
                "FROM trackings AS t " +
                "JOIN startup_trackings AS st " +
                "ON st.tracker_id = " + user.getId() +
                "JOIN attachments AS a " +
                "ON a.attachment_id = t.logo_id " +
                " ORDER BY t.is_active, t.started_at DESC"
        ).into(Tracking.class);
    }

    @Override
    public List<Tracking> getStartupTrackings(User user) {
        return jooq.fetch("" +
                "SELECT DISTINCT(t.*), logo.* " +
                "FROM trackings AS t " +
                "JOIN attachments AS logo " +
                "ON logo.attachment_id = t.logo_id " +
                "JOIN startups AS s " +
                "ON s.owner_id = "+ user.getId() + " " +
                "JOIN startup_trackings AS st " +
                "ON st.startup_id = s.startup_id " +
                "AND st.tracking_id = t.tracking_id " +
                "ORDER BY t.is_active, t.started_at DESC"
        ).into(Tracking.class);
    }
}
