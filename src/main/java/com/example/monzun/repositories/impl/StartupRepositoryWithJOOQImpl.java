package com.example.monzun.repositories.impl;

import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.Startup;
import com.example.monzun.entities.User;
import com.example.monzun.enums.AttachmentPolytableTypeConstants;
import com.example.monzun.repositories.StartupRepositoryWithJOOQ;
import org.jooq.DSLContext;

import java.util.List;

public class StartupRepositoryWithJOOQImpl implements StartupRepositoryWithJOOQ {
    private final DSLContext jooq;

    public StartupRepositoryWithJOOQImpl(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Override
    public List<Startup> getTrackerStartups(User user) {
        return jooq.fetch("" +
                "SELECT DISTINCT(s.*) " +
                "FROM startups AS s " +
                "JOIN trackings AS t " +
                "ON t.is_active = TRUE " +
                "JOIN startup_trackings AS st" +
                "ON st.startup_id = s.startup_id " +
                "AND st.tracker_id =" + user.getId() + "AND st.tracking_id = t.tracking_id"
        ).into(Startup.class);
    }

    @Override
    public List<Startup> getStartupStartups(User user) {
        return jooq.selectFrom("startups")
                .where("owner_id = " + user.getId())
                .fetchInto(Startup.class);

    }

    @Override
    public List<User> getStartupTrackers(Startup startup) {
        return jooq.fetch(
                "SELECT DISTINCT(u.*) " +
                        "FROM users AS u " +
                        "JOIN startup_trackings AS st " +
                        "ON st.startup_id = " + startup.getId() + " " +
                        "AND u.user_id = st.tracker_id")
                .into(User.class);
    }

    @Override
    public List<Attachment> getStartupAttachments(Startup startup) {
        return jooq.fetch(
                "SELECT DISTINCT(a.*) " +
                        "FROM attachments AS a " +
                        "WHERE a.polytable_id = " + startup.getId() + " " +
                        "AND a.polytable_type = '" + AttachmentPolytableTypeConstants.STARTUP.getType() + "'")
                .into(Attachment.class);
    }
}
