package com.example.monzun.exception;

import javax.persistence.EntityNotFoundException;

public class UserByEmailNotFoundException extends EntityNotFoundException {

    public UserByEmailNotFoundException(String msg) {
        super(msg);
    }
}
