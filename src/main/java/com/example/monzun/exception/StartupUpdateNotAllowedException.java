package com.example.monzun.exception;

import com.example.monzun.entities.Startup;
import com.example.monzun.entities.User;

public class StartupUpdateNotAllowedException extends SecurityException {
    public StartupUpdateNotAllowedException(Startup startup, User user) {
        super("Update Startup " + startup.toString() + " not allowed for user" + user.toString());
    }
}
