package com.example.monzun.controllers;


import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.services.TaskStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@Validated
@RestController
@RequestMapping("/api/tasks-statuses")
public class TaskStatusController extends BaseRestController {

    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @GetMapping()
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body(taskStatusService.list());
    }


    @PutMapping("/{taskId}/{taskStatusId}")
    public ResponseEntity<?> setStatus(@PathVariable Long taskId, @PathVariable Integer taskStatusId) {
        try {
            taskStatusService.setStatus(taskId, taskStatusId, getAuthUser());

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
