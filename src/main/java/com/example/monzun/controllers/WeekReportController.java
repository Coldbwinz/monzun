package com.example.monzun.controllers;


import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.exception.WeekReportNotAllowedException;
import com.example.monzun.requests.WeekReportRequest;
import com.example.monzun.services.WeekReportService;
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
import javax.validation.ValidationException;

@Validated
@RestController
@RequestMapping("/api/week-reports")
public class WeekReportController extends BaseRestController {

    private final WeekReportService weekReportService;

    public WeekReportController(WeekReportService weekReportService) {
        this.weekReportService = weekReportService;
    }


    @ApiOperation(value = "Просмотр еженедельного отчета о работе стартапа в наборе")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 404, message = "Отчет не найден"),
    })
    @GetMapping(value = "/{reportId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> show(@ApiParam(required = true, value = "ID отчета") @PathVariable Long reportId) {
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

    @ApiOperation(
            value = "Создание еженедельного отчета о работе стартапа в наборе",
            notes = "Отчет создает только трекер"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 404, message = "Набор или стартап не найден"),
    })
    @PostMapping(value = "/{trackingId}/{startupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(
            @ApiParam(required = true, value = "ID набора") @PathVariable Long trackingId,
            @ApiParam(required = true, value = "ID стартапа") @PathVariable Long startupId,
            @ApiParam @Valid @RequestBody WeekReportRequest weekReportRequest
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


    @ApiOperation(
            value = "Редактирование еженедельного отчета о работе стартапа в наборе",
            notes = "Отчет может редактировать только создатель отчета"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 404, message = "Отчет не найден"),
    })
    @PutMapping("/{reportId}")
    public ResponseEntity<?> update(
            @ApiParam(required = true, value = "ID отчета") @PathVariable Long reportId,
            @ApiParam @RequestBody WeekReportRequest weekReportRequest
    ) {
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


    @ApiOperation(
            value = "Удаление еженедельного отчета о работе стартапа в наборе",
            notes = "Отчет может удалить только создатель отчета"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 404, message = "Отчет не найден"),
    })
    @DeleteMapping("/{reportId}")
    public ResponseEntity<?> delete(@ApiParam(required = true, value = "ID отчета") @PathVariable Long reportId) {
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
