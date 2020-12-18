package com.example.monzun.exception;

public class TrackingAlreadyStartedException extends IllegalStateException {
    public TrackingAlreadyStartedException() {
        super("Cant request to tracking. Tracking already started");
    }
}
