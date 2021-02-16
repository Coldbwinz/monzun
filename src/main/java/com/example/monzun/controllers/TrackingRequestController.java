package com.example.monzun.controllers;


import com.example.monzun.dto.TrackingListDTO;
import com.example.monzun.entities.TrackingRequest;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.exception.TrackingAlreadyStartedException;
import com.example.monzun.exception.TrackingForRequestListAccessException;
import com.example.monzun.services.TrackingRequestService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

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
     *
     * @return JSON
     */
    @ApiOperation(value = "Список наборов для участника")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TrackingListDTO.class),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
    })
    @GetMapping(value = "/trackings", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @ApiOperation(value = "Подача заявка на вступление в набор для участника")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TrackingRequest.class),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 404, message = "Набор или стартап не найден"),
    })
    @PostMapping(value = "/{trackingId}/{startupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(
            @ApiParam(required = true, value = "ID набора") @PathVariable Long trackingId,
            @ApiParam(required = true, value = "ID стартапа") @PathVariable Long startupId
    ) {
        try {
            return ResponseEntity.ok(trackingRequestService.create(trackingId, startupId, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage("not_found", e.getMessage()));
        } catch (TrackingAlreadyStartedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("started_at", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(getErrorMessage("error", e.getMessage()));
        }
    }

    /**
     * Отмена заявки на набор
     *
     * @param trackingId ID набора
     * @param startupId  ID стартапа
     * @return JSON
     */
    @ApiOperation(value = "Отмена заявки в набор для участника")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Набор или стартап не найден"),
    })
    @DeleteMapping(value = "/{trackingId}/{startupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(
            @ApiParam(required = true, value = "ID набора") @PathVariable Long trackingId,
            @ApiParam(required = true, value = "ID стартапа") @PathVariable Long startupId
    ) {
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
