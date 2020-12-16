package com.example.monzun.exception;

import javax.persistence.EntityNotFoundException;

public class FileIsEmptyException extends IllegalStateException {

    public FileIsEmptyException(String msg) {
        super(msg);
    }
}
