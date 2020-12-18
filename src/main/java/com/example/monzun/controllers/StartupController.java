package com.example.monzun.controllers;


import com.example.monzun.dto.StartupDTO;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.exception.StartupAccessNotAllowedException;
import com.example.monzun.exception.StartupCreateNotAllowedException;
import com.example.monzun.requests.StartupRequest;
import com.example.monzun.services.StartupService;
import org.springframework.http.HttpStatus;
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
    @GetMapping()
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
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
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
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody StartupRequest request) {
        try {
            return ResponseEntity.ok(new StartupDTO(startupService.create(request, getAuthUser())));
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
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody StartupRequest request
    ) {
        try {
            return ResponseEntity.ok().body(new StartupDTO(startupService.update(id, request, getAuthUser())));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (StartupCreateNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
