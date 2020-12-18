package com.example.monzun.exception;

public class FileIsEmptyException extends IllegalStateException {

    public FileIsEmptyException(String msg) {
        super(msg);
    }
}
