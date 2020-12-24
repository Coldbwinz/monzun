package com.example.monzun.repositories.impl;

import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.Startup;
import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.WeekReport;
import com.example.monzun.enums.AttachmentPolytableTypeConstants;
import com.example.monzun.repositories.WeekReportRepositoryWithJOOQ;
import org.jooq.DSLContext;

import java.util.List;

public class WeekReportRepositoryWithJOOQImpl implements WeekReportRepositoryWithJOOQ {
    private final DSLContext jooq;

    public WeekReportRepositoryWithJOOQImpl(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Override
    public List<Attachment> getWeekReportAttachments(WeekReport weekReport) {
        return jooq.fetch(
                "SELECT DISTINCT(a.*) " +
                        "FROM attachments AS a " +
                        "WHERE a.polytable_id = " + weekReport.getId() + " " +
                        "AND a.polytable_type = '" + AttachmentPolytableTypeConstants.WEEK_REPORT.getType() + "'")
                .into(Attachment.class);
    }

    @Override
    public List<WeekReport> getWeekReportByTrackingAndStartupAndWeek(Tracking tracking, Startup startup, Integer week) {
        return jooq.fetch(
                "SELECT DISTINCT * " +
                        "FROM week_reports " +
                        "WHERE tracking_id = " + tracking.getId() + " " +
                        "AND startup_id = " + startup.getId() + " " +
                        "AND week BETWEEN 1 AND " + week + " " +
                        "ORDER BY week")
                .into(WeekReport.class);
    }
}
