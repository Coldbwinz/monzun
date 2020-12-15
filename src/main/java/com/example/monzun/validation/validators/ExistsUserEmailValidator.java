package com.example.monzun.validation.validators;

import com.example.monzun.repositories.UserRepository;
import com.example.monzun.validation.rules.ExistsUserEmail;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistsUserEmailValidator implements ConstraintValidator<ExistsUserEmail, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return email != null && userRepository.findByEmail(email).isPresent();
    }
}
