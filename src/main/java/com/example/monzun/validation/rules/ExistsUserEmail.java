package com.example.monzun.validation.rules;

import com.example.monzun.validation.validators.ExistsUserEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ExistsUserEmailValidator.class)
@Documented
public @interface ExistsUserEmail {
    String message() default "User with this email not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
