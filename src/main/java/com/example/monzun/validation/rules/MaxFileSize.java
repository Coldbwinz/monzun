package com.example.monzun.validation.rules;

import com.example.monzun.validation.validators.MaxFileSizeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = MaxFileSizeValidator.class)
@Documented
public @interface MaxFileSize {
    String message() default "File size exceed max size. Max size is 800MB";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
