package com.example.monzun.exception;

import com.example.monzun.entities.Startup;
import com.example.monzun.entities.User;

public class StartupAccessNotAllowedException extends SecurityException {
    public StartupAccessNotAllowedException(Startup startup, User user) {
        super("Startup " + startup.toString() + " not allowed for user" + user.toString());
    }
}
