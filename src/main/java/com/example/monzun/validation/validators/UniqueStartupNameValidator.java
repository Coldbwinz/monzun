package com.example.monzun.validation.validators;

import com.example.monzun.repositories.StartupRepository;
import com.example.monzun.validation.rules.UniqueStartupName;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueStartupNameValidator implements ConstraintValidator<UniqueStartupName, String> {
    @Autowired
    private StartupRepository startupRepository;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        return name != null && !startupRepository.findByName(name).isPresent();
    }
}
