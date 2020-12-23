package com.example.monzun.validation.validators;

import com.example.monzun.repositories.TaskStatusRepository;
import com.example.monzun.validation.rules.ExistsTaskStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistsTaskStatusValidator implements ConstraintValidator<ExistsTaskStatus, Integer> {

    @Autowired
    TaskStatusRepository taskStatusRepository;

    @Override
    public boolean isValid(Integer taskStatusId, ConstraintValidatorContext constraintValidatorContext) {
        return taskStatusRepository.existsByTaskStatusId(taskStatusId);
    }
}
