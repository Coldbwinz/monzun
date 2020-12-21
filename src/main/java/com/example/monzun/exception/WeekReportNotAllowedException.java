package com.example.monzun.exception;

import com.example.monzun.entities.Startup;
import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.User;

public class WeekReportNotAllowedException extends SecurityException {
    public WeekReportNotAllowedException(Tracking tracking, Startup startup, User user) {
        super("Week report for tracking" + tracking.toString() +
                " and startup " + startup.toString() +
                " not allowed for user" + user.toString());
    }
}
