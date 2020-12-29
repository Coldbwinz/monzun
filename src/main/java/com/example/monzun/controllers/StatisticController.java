package com.example.monzun.controllers;


import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.services.StatisticService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;

@Validated
@RestController
@RequestMapping("/api/stats")
public class StatisticController extends BaseRestController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @ApiOperation(
            value = "Просмотр статистики стартапа",
            notes = "Статистика стартапа в наборе это сборник отчетов трекера по неделям с его оценками" +
                    " и средняя оценка по текущим неделям"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Набор или стартап не найден"),
    })
    @GetMapping("/{trackingId}/{startupId}")
    public ResponseEntity<?> getStats(
            @ApiParam(required = true, value = "ID набора") @PathVariable Long trackingId,
            @ApiParam(required = true, value = "ID стартапа") @PathVariable Long startupId
    ) {
        try {
            return ResponseEntity.ok(statisticService.get(trackingId, startupId, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }
}
