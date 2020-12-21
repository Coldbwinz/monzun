package com.example.monzun.controllers;

import com.example.monzun.entities.User;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

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
}
