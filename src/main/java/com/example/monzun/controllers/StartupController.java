package com.example.monzun.controllers;


import com.example.monzun.dto.StartupDTO;
import com.example.monzun.dto.StartupListDTO;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.exception.StartupAccessNotAllowedException;
import com.example.monzun.exception.StartupCreateNotAllowedException;
import com.example.monzun.requests.StartupRequest;
import com.example.monzun.services.StartupService;
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
import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/startups")
public class StartupController extends BaseRestController {

    private final StartupService startupService;

    public StartupController(StartupService startupService) {
        this.startupService = startupService;
    }

    /**
     * Список стартапов.
     *
     * @return JSON
     */
    @ApiOperation(value = "Список стартапов")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = StartupListDTO.class),
            @ApiResponse(code = 401, message = "Пользователь не авторизован")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(startupService.getStartups(getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Детальный просмотр стартапа
     *
     * @param id Startup id
     * @return JSON
     */
    @ApiOperation(value = "Просмотр конкретного стартапа")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = StartupDTO.class),
            @ApiResponse(code = 404, message = "Стартап не найден"),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> show(
            @ApiParam(required = true, value = "ID стартапа") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(startupService.getStartup(id, getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (StartupAccessNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    this.getErrorMessage("startup", "This startup not allowed for this user"));
        }
    }


    /**
     * Создание стартапа
     *
     * @param request StartupRequest
     * @return JSON
     */
    @ApiOperation(value = "Создание стартапа")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = StartupDTO.class),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@ApiParam @Valid @RequestBody StartupRequest request) {
        try {
            return ResponseEntity.ok(new StartupDTO(startupService.create(request, getAuthUser())));
        } catch (IllegalStateException e) {
           return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(getErrorMessage("name", e.getMessage()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (StartupCreateNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Редактирование стартапа
     *
     * @param id      Startup id
     * @param request StartupRequest
     * @return JSON
     */
    @ApiOperation(value = "Редактирование стартапа")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = StartupDTO.class),
            @ApiResponse(code = 403, message = "Доступ запрещен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Стартап не найден"),
    })
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(
            @ApiParam(required = true, value = "ID стартапа") @PathVariable Long id,
            @ApiParam @Valid @RequestBody StartupRequest request
    ) {
        try {
            return ResponseEntity.ok().body(new StartupDTO(startupService.update(id, request, getAuthUser())));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (StartupCreateNotAllowedException | StartupAccessNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(this.getErrorMessage("forbidden", e.getMessage()));
        }
    }
}
