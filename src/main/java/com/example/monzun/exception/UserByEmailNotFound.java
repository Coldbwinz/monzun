package com.example.monzun.exception;

import javax.persistence.EntityNotFoundException;

public class UserByEmailNotFound extends EntityNotFoundException {

    public UserByEmailNotFound(String msg) {
        super(msg);
    }
}
