package com.example.monzun.validation.validators;

import com.example.monzun.enums.WeekReportEstimatesEnum;
import com.example.monzun.validation.rules.ValidWeekReportEstimate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValidWeekReportEstimateValidator implements ConstraintValidator<ValidWeekReportEstimate, Integer> {

    @Override
    public boolean isValid(Integer estimate, ConstraintValidatorContext constraintValidatorContext) {
        List<Integer> scores = Arrays.stream(WeekReportEstimatesEnum
                .values())
                .sequential()
                .map(WeekReportEstimatesEnum::getScore).collect(Collectors.toList());

        return scores.contains(estimate);
    }
}
