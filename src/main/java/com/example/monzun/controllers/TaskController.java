package com.example.monzun.controllers;


import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.requests.TaskRequest;
import com.example.monzun.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/tasks")
public class TaskController extends BaseRestController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{trackingId}/{startupId}")
    public ResponseEntity<?> list(@PathVariable Long trackingId, @PathVariable Long startupId) {
        try {
            return ResponseEntity.ok().body(taskService.list(trackingId, startupId, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> show(@PathVariable Long taskId) {
        try {
            return ResponseEntity.ok().body(taskService.show(taskId, getAuthUser()));
        } catch (NoAuthUserException    e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }

    @PostMapping("/{trackingId}/{startupId}")
    public ResponseEntity<?> create(
            @PathVariable Long trackingId,
            @PathVariable Long startupId,
            @Valid @RequestBody TaskRequest taskRequest
    ) {
        try {
            return ResponseEntity.ok().body(taskService.create(trackingId, startupId, taskRequest, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> update(@PathVariable Long taskId, @Valid @RequestBody TaskRequest taskRequest) {
        try {
            return ResponseEntity.ok().body(taskService.update(taskId, taskRequest, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> delete(@PathVariable Long taskId) {
        try {
            taskService.delete(taskId, getAuthUser());

            return ResponseEntity.ok().body(getTrueResponse());
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }
}
