package com.example.monzun.controllers;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовый класс для REST контроллера.
 */
public abstract class BaseRestController {
    /**
     * @return Структура успешного запроса (success:true)
     */
    protected Map<String, Boolean> getFalseResponse() {
        Map<String, Boolean> successFalse = new HashMap<>();
        successFalse.put("success", false);

        return successFalse;
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
