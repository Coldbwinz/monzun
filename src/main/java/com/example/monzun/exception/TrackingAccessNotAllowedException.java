package com.example.monzun.exception;

import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.User;

public class TrackingAccessNotAllowedException extends SecurityException {
    public TrackingAccessNotAllowedException(Tracking tracking, User user) {
        super("Tracking " + tracking.toString() + " not allowed for user" + user.toString());
    }
}
