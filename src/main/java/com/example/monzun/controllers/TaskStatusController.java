package com.example.monzun.controllers;


import com.example.monzun.dto.TaskStatusDTO;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.services.TaskStatusService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/tasks-statuses")
public class TaskStatusController extends BaseRestController {

    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @ApiOperation(value = "Список статусов задачи")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TaskStatusDTO.class),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body(taskStatusService.list());
    }


    @ApiOperation(value = "Изменение статуса задачи")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TaskStatusDTO.class),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 404, message = "Задача или статус не найдены")
    })
    @PutMapping(value = "/{taskId}/{taskStatusId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setStatus(
            @ApiParam(required = true, value = "ID задачи") @PathVariable Long taskId,
            @ApiParam(required = true, value = "ID статуса задачи") @Min(1) @Max(5) @PathVariable Integer taskStatusId
    ) {
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
