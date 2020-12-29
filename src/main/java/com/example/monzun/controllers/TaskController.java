package com.example.monzun.controllers;


import com.example.monzun.dto.TaskDTO;
import com.example.monzun.dto.TaskListDTO;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.requests.TaskRequest;
import com.example.monzun.services.TaskService;
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
import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/tasks")
public class TaskController extends BaseRestController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @ApiOperation(value = "Список задач для стартапа в наборе")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TaskListDTO.class),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Набор или стартап не найден"),
    })
    @GetMapping(value = "/{trackingId}/{startupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(
            @ApiParam(required = true, value = "ID набора") @PathVariable Long trackingId,
            @ApiParam(required = true, value = "ID стартапа") @PathVariable Long startupId) {
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


    @ApiOperation(value = "Просмотр задачи")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TaskDTO.class),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Задача не найдена"),
    })
    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> show(@ApiParam(required = true, value = "ID задачи") @PathVariable Long taskId) {
        try {
            return ResponseEntity.ok().body(taskService.show(taskId, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }


    @ApiOperation(value = "Создание задачи", notes = "Задачи может создавать только трекер")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TaskDTO.class),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
    })
    @PostMapping(value = "/{trackingId}/{startupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(
            @ApiParam(required = true, value = "ID набора") @PathVariable Long trackingId,
            @ApiParam(required = true, value = "ID стартапа") @PathVariable Long startupId,
            @ApiParam @Valid @RequestBody TaskRequest taskRequest
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


    @ApiOperation(value = "Редактирование задачи", notes = "Задачи может редактировать только создатель задачи")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TaskDTO.class),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Задачи не найдена"),
    })
    @PutMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(
            @ApiParam(required = true, value = "ID задачи") @PathVariable Long taskId,
            @ApiParam @Valid @RequestBody TaskRequest taskRequest) {
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


    @ApiOperation(value = "Удаление задачи", notes = "Задачи может удалять только создатель задачи")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TaskDTO.class),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Задачи не найдена"),
    })
    @DeleteMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@ApiParam(required = true, value = "ID задачи") @PathVariable Long taskId) {
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
