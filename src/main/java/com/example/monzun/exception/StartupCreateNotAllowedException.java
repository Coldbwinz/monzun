package com.example.monzun.exception;

import com.example.monzun.entities.User;

public class StartupCreateNotAllowedException extends SecurityException {
    public StartupCreateNotAllowedException(User user) {
        super("Create startups not allowed for user" + user.toString());
    }
}
