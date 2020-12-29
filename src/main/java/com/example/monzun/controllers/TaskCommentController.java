package com.example.monzun.controllers;


import com.example.monzun.dto.TaskCommentDTO;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.requests.TaskCommentRequest;
import com.example.monzun.services.TaskCommentService;
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
@RequestMapping("/api/task-comments")
public class TaskCommentController extends BaseRestController {

    private final TaskCommentService taskCommentService;

    public TaskCommentController(TaskCommentService taskCommentService) {
        this.taskCommentService = taskCommentService;
    }

    @ApiOperation(value = "Список комментариев к задаче")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TaskCommentDTO.class),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Задача не найдена"),
    })
    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(@ApiParam(required = true, value = "ID задачи") @PathVariable Long taskId) {
        try {
            return ResponseEntity.ok(taskCommentService.list(taskId, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }


    @ApiOperation(
            value = "Добавление комментария к задаче",
            notes = "Добавить комментарий к задаче можно только в случае, когда пользователь имеет доступ к самой задаче"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TaskCommentDTO.class),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
    })
    @PostMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(
            @ApiParam(required = true, value = "ID задачи") @PathVariable Long taskId,
            @ApiParam @Valid @RequestBody TaskCommentRequest request) {
        try {
            return ResponseEntity.ok(taskCommentService.create(taskId, request, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }


    @ApiOperation(value = "Редактирование комментария к задаче", notes = "Редактировать можно только свои комментарии")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TaskCommentDTO.class),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Задача не найдена"),
    })
    @PutMapping(value = "/{taskCommentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(
            @ApiParam(required = true, value = "ID задачи") @PathVariable Long taskCommentId,
            @ApiParam @Valid @RequestBody TaskCommentRequest request) {
        try {
            return ResponseEntity.ok(taskCommentService.update(taskCommentId, request, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }

    @ApiOperation(value = "Удаление комментария к задаче", notes = "Удалить можно только свой комментарий")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Задача не найдена"),
    })
    @DeleteMapping(value = "/{taskCommentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@ApiParam(required = true, value = "ID задачи") @PathVariable Long taskCommentId) {
        try {
            taskCommentService.delete(taskCommentId, getAuthUser());

            return ResponseEntity.ok().body(getTrueResponse());
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        }
    }
}
