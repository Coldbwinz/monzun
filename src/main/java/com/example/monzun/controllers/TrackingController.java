package com.example.monzun.controllers;


import com.example.monzun.dto.TrackingListDTO;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.exception.TrackingAccessNotAllowedException;
import com.example.monzun.services.TrackingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;

@Validated
@RestController
@RequestMapping("/api/trackings")
public class TrackingController extends BaseRestController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    /**
     * Список наборов.
     *
     * @return JSON
     */

    @ApiOperation(value = "Список наборов")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TrackingListDTO.class),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(trackingService.getTrackings(getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Детальный просмотр набора
     *
     * @param id Tracking id
     * @return JSON
     */
    @ApiOperation(value = "Детальный просмотр набора")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = TrackingListDTO.class),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 404, message = "Набор не найден"),
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> show(@ApiParam(required = true, value = "ID набора") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(trackingService.getTracking(id, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (TrackingAccessNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMessage("forbidden", e.getMessage()));
        }
    }
}
