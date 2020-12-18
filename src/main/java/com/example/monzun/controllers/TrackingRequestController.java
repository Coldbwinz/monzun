package com.example.monzun.controllers;


import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.exception.TrackingAlreadyStartedException;
import com.example.monzun.exception.TrackingForRequestListAccessException;
import com.example.monzun.services.TrackingRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@Validated
@RestController
@RequestMapping("/api/requests")
public class TrackingRequestController extends BaseRestController {

    private final TrackingRequestService trackingRequestService;

    public TrackingRequestController(TrackingRequestService trackingRequestService) {
        this.trackingRequestService = trackingRequestService;
    }

    /**
     * Просмотр списка наборов для записи. Доступен только участнику
     * @return JSON
     */
    @GetMapping("/trackings")
    public ResponseEntity<?> trackingList() {
        try {
            return ResponseEntity.ok().body(trackingRequestService.getTrackingList(getAuthUser()));
        } catch (TrackingForRequestListAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    /**
     * Подать заявку на набор
     *
     * @param trackingId ID набора
     * @param startupId  ID стартапа
     * @return JSON
     */
    @PostMapping("/{trackingId}/{startupId}")
    public ResponseEntity<?> create(@PathVariable Long trackingId, @PathVariable Long startupId) {
        try {
            return ResponseEntity.ok(trackingRequestService.create(trackingId, startupId, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (TrackingAlreadyStartedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("started_at", e.getMessage()));
        }
    }

    /**
     * Отмена заявки на набор
     *
     * @param trackingId ID набора
     * @param startupId  ID стартапа
     * @return JSON
     */
    @DeleteMapping("/{trackingId}/{startupId}")
    public ResponseEntity<?> delete(@PathVariable Long trackingId, @PathVariable Long startupId) {
        try {
            trackingRequestService.delete(trackingId, startupId, getAuthUser());
            return ResponseEntity.ok(this.getTrueResponse());
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        }
    }
}
