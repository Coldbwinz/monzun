package com.example.monzun.validation.rules;

import com.example.monzun.validation.validators.ValidWeekReportEstimateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidWeekReportEstimateValidator.class)
@Documented
public @interface ValidWeekReportEstimate {
    String message() default "Estimate is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
