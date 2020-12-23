package com.example.monzun.controllers;


import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.exception.WeekReportNotAllowedException;
import com.example.monzun.requests.WeekReportRequest;
import com.example.monzun.services.WeekReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

@Validated
@RestController
@RequestMapping("/api/week-reports")
public class WeekReportController extends BaseRestController {

    private final WeekReportService weekReportService;

    public WeekReportController(WeekReportService weekReportService) {
        this.weekReportService = weekReportService;
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<?> show(@PathVariable Long reportId) {
        try {
            return ResponseEntity.ok(weekReportService.show(reportId, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidder", e.getMessage()));
        }
    }

    @PostMapping("/{trackingId}/{startupId}")
    public ResponseEntity<?> create(
            @PathVariable Long trackingId,
            @PathVariable Long startupId,
            @RequestBody WeekReportRequest weekReportRequest
    ) {
        try {
            return ResponseEntity.ok()
                    .body(weekReportService.create(trackingId, startupId, weekReportRequest, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (WeekReportNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(getErrorMessage("error", e.getMessage()));
        }
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<?> update(@PathVariable Long reportId, @RequestBody WeekReportRequest weekReportRequest) {
        try {
            return ResponseEntity.ok().body(weekReportService.update(reportId, weekReportRequest, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (WeekReportNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(getErrorMessage("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<?> delete(@PathVariable Long reportId) {
        try {
            weekReportService.delete(reportId, getAuthUser());

            return ResponseEntity.ok().body(getTrueResponse());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (WeekReportNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }
}
