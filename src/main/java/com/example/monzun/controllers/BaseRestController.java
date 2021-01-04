package com.example.monzun.controllers;

import com.example.monzun.entities.User;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.repositories.UserRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

/**
 * Базовый класс для REST контроллера.
 */
public abstract class BaseRestController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Получение текущего авторизованного пользователя
     *
     * @return User авториванный пользователь
     * @throws NoAuthUserException NoAuthUserException
     */
    protected User getAuthUser() throws NoAuthUserException {
        return userRepository.findByEmail(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        ).orElseThrow(NoAuthUserException::new);
    }

    /**
     * @return Структура неудачного запроса (success:false)
     */
    protected Map<String, Boolean> getTrueResponse() {
        Map<String, Boolean> successFalse = new HashMap<>();
        successFalse.put("success", true);

        return successFalse;
    }

    /**
     * @param errorK Ключ ошибки
     * @param errorV Описание ошибки
     * @return Структура, содержарщая информацию об ошибке
     */
    protected Map<String, Map<String, String>> getErrorMessage(String errorK, String errorV) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(errorK, errorV);
        Map<String, Map<String, String>> errors = new HashMap<>();
        errors.put("errors", errorMap);
        return errors;
    }



    /**
     * Валидация ConstraintViolationException
     * @param e ConstraintViolationException
     * @return JSON
     */
    @ExceptionHandler(value = { ConstraintViolationException.class })
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public JSONObject handleResourceNotFoundException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        JSONObject errors = new JSONObject();
        List<String> errorMessages = new ArrayList<>();

        for (ConstraintViolation<?> violation : violations ) {
            errorMessages.add(violation.getMessage());
        }

        errors.put("errors", errorMessages);
        return errors;
    }
}
